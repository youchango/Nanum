package com.nanum.domain.file.service;

import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import com.nanum.domain.file.repository.FileStoreRepository;
import com.nanum.domain.file.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FileService {

    private final FileStoreRepository fileStoreRepository;
    private final FileStorageProperties fileStorageProperties;

    /**
     * 파일을 저장소에 업로드하고 DB에 메타데이터를 저장합니다.
     */
    @Transactional
    public FileStore uploadFile(MultipartFile file, ReferenceType type, String refId, boolean isMain) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        try {
            // 1. 저장 경로 생성 (ReferenceType/YYYY-MM-DD)
            String datePath = java.time.LocalDate.now().toString();
            String subPath = type.name() + "/" + datePath;

            // 2. 물리적 저장 실행
            String saveName = generateSaveName(file.getOriginalFilename());
            String relativePath = saveToStorage(file, subPath, saveName);

            // 3. DB 메타데이터 저장
            FileStore fileStore = FileStore.builder()
                    .referenceType(type)
                    .referenceId(refId)
                    .orgName(StringUtils.cleanPath(file.getOriginalFilename()))
                    .saveName(saveName)
                    .path(relativePath)
                    .ext(getFileExtension(file.getOriginalFilename()))
                    .size(file.getSize())
                    .isMain(isMain ? "Y" : "N")
                    .displayOrder(0)
                    .build();

            return fileStoreRepository.save(fileStore);

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 저장소 설정에 따라 파일을 물리적으로 저장합니다.
     */
    private String saveToStorage(MultipartFile file, String subPath, String saveName) throws IOException {
        String storageType = fileStorageProperties.getType();

        if ("local".equalsIgnoreCase(storageType)) {
            // 로컬 저장
            String baseDir = fileStorageProperties.getLocal().getPath();
            Path uploadPath = Paths.get(baseDir, subPath).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(saveName);
            file.transferTo(filePath.toFile());

            log.info("Local storage save success: {}", filePath);
            return "/" + subPath + "/" + saveName;
        } else if ("remote".equalsIgnoreCase(storageType) || "server".equalsIgnoreCase(storageType)) {
            // 원격/서버 저장 (추후 구현 예정)
            log.warn("Remote storage upload requested but not fully implemented. Host: {}",
                    fileStorageProperties.getRemote().getHost());
            throw new UnsupportedOperationException("원격 서버 저장소 기능은 준비 중입니다.");
        } else {
            throw new IllegalArgumentException("알 수 없는 저장소 타입입니다: " + storageType);
        }
    }

    private String generateSaveName(String originalFilename) {
        String ext = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
    }

    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename))
            return "";
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex > -1 ? filename.substring(dotIndex + 1) : "";
    }

    public List<FileStore> getFiles(ReferenceType type, String refId) {
        return fileStoreRepository.findByReferenceTypeAndReferenceIdOrderByDisplayOrderAsc(type, refId);
    }

    public List<FileStore> getFiles(ReferenceType type, List<String> refIds) {
        if (refIds == null || refIds.isEmpty()) {
            return List.of();
        }
        return fileStoreRepository.findByReferenceTypeAndReferenceIdIn(type, refIds);
    }

    /**
     * 파일의 전체 접근 URL을 생성합니다.
     */
    public String getFullUrl(String dbPath) {
        if (!StringUtils.hasText(dbPath) || dbPath.startsWith("http")) {
            return dbPath;
        }

        String serverDomain = fileStorageProperties.getServerDomain();
        if ("local".equalsIgnoreCase(fileStorageProperties.getType())) {
            // 로컬인 경우 정적 리소스 매핑 경로(/resources/upload)를 추가
            return serverDomain + "/resources/upload" + (dbPath.startsWith("/") ? dbPath : "/" + dbPath);
        } else {
            return serverDomain + (dbPath.startsWith("/") ? dbPath : "/" + dbPath);
        }
    }

    public FileStore getMainFile(ReferenceType type, String refId) {
        return fileStoreRepository.findByReferenceTypeAndReferenceIdOrderByDisplayOrderAsc(type, refId)
                .stream()
                .filter(f -> "Y".equals(f.getIsMain()))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void deleteByReference(ReferenceType type, String refId, String memberCode) {
        List<FileStore> files = getFiles(type, refId);
        for (FileStore file : files) {
            file.delete(memberCode);
        }
    }

    @Transactional
    public void deleteFile(String fileId) {
        FileStore fileStore = fileStoreRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 물리 파일 삭제 (로컬인 경우만 예시 구현)
        if ("local".equalsIgnoreCase(fileStorageProperties.getType())) {
            try {
                Path filePath = Paths.get(fileStorageProperties.getLocal().getPath(), fileStore.getPath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("물리 파일 삭제 실패: {}", fileStore.getPath());
            }
        }

        fileStoreRepository.delete(fileStore);
    }

    @Transactional
    public void updateFileReference(List<String> fileIds, ReferenceType type, String refId) {
        if (fileIds == null || fileIds.isEmpty())
            return;

        List<FileStore> files = fileStoreRepository.findAllById(fileIds);
        for (FileStore file : files) {
            file.updateReference(type, refId);
        }
    }

    /**
     * 파일 참조 정보를 동기화합니다. (기존 파일 중 리스트에 없는 파일 삭제 포함)
     */
    @Transactional
    public void syncFiles(List<String> newFileIds, ReferenceType type, String refId) {
        // 1. 기존에 해당 참조로 등록된 파일 조회
        List<FileStore> existingFiles = getFiles(type, refId);
        
        // 2. 삭제 대상 식별 (기존 파일 중 새 리스트에 없는 것)
        List<FileStore> filesToDelete = existingFiles.stream()
                .filter(ef -> newFileIds == null || !newFileIds.contains(ef.getFileId()))
                .collect(Collectors.toList());
        
        // 3. 파일 삭제 수행
        for (FileStore file : filesToDelete) {
            deleteFile(file.getFileId());
        }
        
        // 4. 신규 파일들 참조 업데이트
        if (newFileIds != null && !newFileIds.isEmpty()) {
            updateFileReference(newFileIds, type, refId);
        }
    }

    @Transactional
    public void setMainImage(String fileId, ReferenceType type, String refId) {
        fileStoreRepository.updateMainStatus(type, refId, "N");
        fileStoreRepository.updateMainStatusById(fileId, "Y");
    }
}

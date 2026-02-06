package com.nanum.domain.file.service;

import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import com.nanum.domain.file.repository.FileStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FileService {

    private final FileStoreRepository fileStoreRepository;

    @Value("${file.storage.local.path:./uploads}")
    private String uploadDir;

    @Value("${file.storage.type:LOCAL}")
    private String storageType;

    @Value("${file.storage.server-domain:http://localhost:8080}")
    private String serverDomain;

    @Transactional
    public FileStore uploadFile(MultipartFile file, ReferenceType type, String refId, boolean isMain) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        try {
            // 1. 디렉토리 생성
            String datePath = java.time.LocalDate.now().toString(); // YYYY-MM-DD
            Path uploadPath = Paths.get(uploadDir, type.name(), datePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. 파일 저장
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            if (originalFilename.lastIndexOf(".") > -1) {
                ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }
            String saveName = UUID.randomUUID().toString() + "." + ext;
            Path filePath = uploadPath.resolve(saveName);
            file.transferTo(filePath.toFile());

            // 3. DB 저장 (상대 경로로 저장: /ReferenceType/YYYY-MM-DD/filename)
            // Windows 구분자(\)를 표준(/)으로 변경
            String dbPath = "/" + type.name() + "/" + datePath + "/" + saveName;

            FileStore fileStore = FileStore.builder()
                    .referenceType(type)
                    .referenceId(refId)
                    .orgName(originalFilename)
                    .saveName(saveName)
                    .path(dbPath) // DB에는 상대 경로만 저장
                    .ext(ext)
                    .size(file.getSize())
                    .isMain(isMain ? "Y" : "N")
                    .displayOrder(0)
                    .build();

            return fileStoreRepository.save(fileStore);

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public List<FileStore> getFiles(ReferenceType type, String refId) {
        List<FileStore> files = fileStoreRepository.findByReferenceTypeAndReferenceIdOrderByDisplayOrderAsc(type,
                refId);
        // 조회 시 Full URL로 변환하여 반환 (DTO 변환 시 처리하거나 여기서 처리)
        // FileStore 엔티티의 path는 DB값이므로, DTO 변환 시 Full URL을 조합하도록 유도하거나,
        // 필요하다면 여기서 Transient 필드 등을 채울 수 있음.
        // 현재는 FileStore 객체 그대로 반환하므로, 사용하는 쪽에서 getUrl() 등을 호출할 수 있게 하거나
        // DTO 변환 로직에서 getFullUrl()을 사용해야 함.
        return files;
    }

    // Helper method to generate full URL
    public String getFullUrl(String dbPath) {
        if (StringUtils.hasText(dbPath) && !dbPath.startsWith("http")) {
            if ("SERVER".equalsIgnoreCase(storageType)) {
                return serverDomain + dbPath;
            } else {
                // LOCAL: /uploads + dbPath -> http://host/uploads... but actually
                // usually LOCAL means sending relative path or static resource mapping.
                // If we want absolute URL even for LOCAL:
                return serverDomain + "/uploads" + dbPath;
            }
        }
        return dbPath;
    }

    /**
     * Soft delete files by reference
     */
    @Transactional
    public void deleteByReference(ReferenceType type, String refId, String memberCode) {
        List<FileStore> files = fileStoreRepository.findByReferenceTypeAndReferenceIdOrderByDisplayOrderAsc(type,
                refId);
        for (FileStore file : files) {
            file.delete(memberCode);
        }
    }

    @Transactional
    public void deleteFile(String fileId) {
        FileStore fileStore = fileStoreRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 물리 파일 삭제
        try {
            // DB Path: /PRODUCT/2026-02-06/uuid.jpg
            // Local Path: ./uploads + /PRODUCT/2026-02-06/uuid.jpg
            Path filePath = Paths.get(uploadDir, fileStore.getPath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("물리 파일 삭제 실패: {}", fileStore.getPath());
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

    @Transactional
    public void setMainImage(String fileId, ReferenceType type, String refId) {
        fileStoreRepository.updateMainStatus(type, refId, "N");
        fileStoreRepository.updateMainStatusById(fileId, "Y");
    }
}

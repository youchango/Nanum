package com.nanum.domain.file.controller;

import com.nanum.domain.file.dto.FileResponseDTO;
import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import com.nanum.domain.file.service.FileService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "전사 공통 파일 관리 API")
public class FileController implements ResponseSupport {

    private final FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "파일을 업로드하고 DB에 메타데이터를 저장합니다.")
    public ResponseEntity<ApiResponse<FileResponseDTO>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("referenceType") ReferenceType referenceType,
            @RequestParam("referenceId") String referenceId,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain) throws IOException {

        FileStore fileStore = fileService.uploadFile(file, referenceType, referenceId, isMain);
        return success(FileResponseDTO.from(fileStore));
    }

    @GetMapping("/{referenceType}/{referenceId}")
    @Operation(summary = "파일 목록 조회", description = "특정 참조 대상의 파일 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FileResponseDTO>>> getFiles(
            @PathVariable ReferenceType referenceType,
            @PathVariable String referenceId) {

        List<FileResponseDTO> dtos = fileService.getFiles(referenceType, referenceId).stream()
                .map(FileResponseDTO::from)
                .collect(Collectors.toList());

        return success(dtos);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String fileId) {
        fileService.deleteFile(fileId);
        return success();
    }

    @PatchMapping("/{fileId}/main")
    @Operation(summary = "대표 이미지 설정", description = "해당 파일을 대표 이미지로 설정합니다.")
    public ResponseEntity<ApiResponse<Void>> setMainImage(
            @PathVariable String fileId,
            @RequestParam("referenceType") ReferenceType referenceType,
            @RequestParam("referenceId") String referenceId) {

        fileService.setMainImage(fileId, referenceType, referenceId);
        return success();
    }

    /**
     * 에디터 전용 업로드 API
     * CKEditor / Tiptap 등의 에디터 규격에 맞는 JSON 응답을 반환합니다.
     */
    @PostMapping("/editor-upload")
    @Operation(summary = "에디터 파일 업로드", description = "에디터 전용 파일 업로드 API입니다. DB에 EDITOR 타입으로 저장됩니다.")
    public ResponseEntity<java.util.Map<String, Object>> editorUpload(
            @RequestParam("upload") MultipartFile file) {
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            // 에디터 업로드는 ReferenceType.EDITOR, referenceId는 "EDITOR" (또는 세션 정보 등)로 저장
            FileStore fileStore = fileService.uploadFile(file, ReferenceType.EDITOR, "EDITOR", false);
            String fullUrl = fileService.getFullUrl(fileStore.getPath());

            response.put("uploaded", true);
            response.put("url", fullUrl);
            response.put("fileId", fileStore.getFileId()); // 추후 관리를 위해 ID도 포함

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("uploaded", false);
            response.put("error", java.util.Map.of("message", "파일 업로드에 실패했습니다. " + e.getMessage()));
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

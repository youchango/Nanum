package com.nanum.global.file.controller;

import com.nanum.domain.file.dto.FileResponseDTO;
import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import com.nanum.domain.file.service.FileService;
import com.nanum.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "전사 공통 파일 관리 API")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "파일을 업로드하고 DB에 메타데이터를 저장합니다.")
    public ResponseEntity<ApiResponse<FileResponseDTO>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("referenceType") ReferenceType referenceType,
            @RequestParam("referenceId") String referenceId,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain) {

        FileStore fileStore = fileService.uploadFile(file, referenceType, referenceId, isMain);
        return ResponseEntity.ok(ApiResponse.success(FileResponseDTO.from(fileStore)));
    }

    @GetMapping("/{referenceType}/{referenceId}")
    @Operation(summary = "파일 목록 조회", description = "특정 참조 대상의 파일 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FileResponseDTO>>> getFiles(
            @PathVariable ReferenceType referenceType,
            @PathVariable String referenceId) {

        List<FileStore> files = fileService.getFiles(referenceType, referenceId);
        List<FileResponseDTO> dtos = files.stream()
                .map(FileResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{fileId}/main")
    @Operation(summary = "대표 이미지 설정", description = "해당 파일을 대표 이미지로 설정합니다.")
    public ResponseEntity<ApiResponse<Void>> setMainImage(
            @PathVariable String fileId,
            @RequestParam("referenceType") ReferenceType referenceType,
            @RequestParam("referenceId") String referenceId) {

        fileService.setMainImage(fileId, referenceType, referenceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

package com.nanum.global.common.controller;

import com.nanum.global.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Tag(name = "Common", description = "Common API")
@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonController {

    private final FileStorageService fileStorageService;

    /**
     * 파일 업로드 API
     * CKEditor ???먮뵒?곗뿉???대?juven ?낅줈?????ъ슜
     */
    @Operation(summary = "에디터 파일 업로드", description = "CKEditor 5 에디터 전용 파일 업로드 API입니다. 에디터 규격에 맞는 JSON 응답을 반환합니다.")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("upload") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "editor") String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            String uploadedUrl = fileStorageService.upload(file, type);

            // CKEditor expects specific JSON response
            response.put("uploaded", true);
            response.put("url", uploadedUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("File Upload Failed", e);
            response.put("uploaded", false);
            response.put("error", Map.of("message", "파일 업로드에 실패했습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

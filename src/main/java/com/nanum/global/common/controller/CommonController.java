package com.nanum.global.common.controller;

import com.nanum.global.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonController {

    private final FileStorageService fileStorageService;

    /**
     * 怨듯넻 ?뚯씪 ?낅줈??API
     * CKEditor ???먮뵒?곗뿉???대?吏 ?낅줈?????ъ슜
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("upload") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "editor") String type
    ) {
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
            response.put("error", Map.of("message", "?뚯씪 ?낅줈?쒖뿉 ?ㅽ뙣?덉뒿?덈떎."));
            return ResponseEntity.status(500).body(response);
        }
    }
}

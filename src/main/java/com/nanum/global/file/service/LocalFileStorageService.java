
package com.nanum.global.file.service;

import com.nanum.global.file.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public String upload(MultipartFile file, String subPath) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Configured Absolute Path (e.g., /Users/.../uploads/)
        String baseUploadPath = fileStorageProperties.getLocal().getPath();
        
        // Full Path (e.g., /Users/.../uploads/banner)
        Path uploadPath = Paths.get(baseUploadPath, subPath).toAbsolutePath().normalize();

        log.info("Physical Upload Path: {}", uploadPath); // Debug log

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate Filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        
        String savedFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(savedFilename);

        // Save File
        file.transferTo(filePath.toFile());

        log.info("Local File Upload Success: {}", filePath);

        // Return Logical URL Path (Must match WebMvcConfig resource handler)
        // /resources/upload/banner/filename.jpg
        return "/resources/upload" + (subPath.startsWith("/") ? subPath : "/" + subPath) + "/" + savedFilename;
    }

    @Override
    public void delete(String filePath) {
        try {
            if (StringUtils.hasText(filePath)) {
                // filePath: /resources/upload/banner/filename.jpg
                // We need to map this back to physical path
                // /resources/upload -> baseUploadPath
                
                String logicalPrefix = "/resources/upload";
                if (filePath.startsWith(logicalPrefix)) {
                    String relativePath = filePath.substring(logicalPrefix.length());
                    // relativePath: /banner/filename.jpg
                    
                    String baseUploadPath = fileStorageProperties.getLocal().getPath();
                    Path absolutePath = Paths.get(baseUploadPath, relativePath);
                    
                    Files.deleteIfExists(absolutePath);
                    log.info("Local File Delete Success: {}", absolutePath);
                }
            }
        } catch (IOException e) {
            log.error("Local File Delete Failed: {}", filePath, e);
        }
    }
}

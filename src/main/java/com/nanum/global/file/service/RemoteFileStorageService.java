package com.nanum.global.file.service;

import com.nanum.global.file.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RemoteFileStorageService implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public String upload(MultipartFile file, String subPath) throws IOException {
        log.info("Attempting Remote Upload to host: {}", fileStorageProperties.getRemote().getHost());
        log.info("Target Path: {}", fileStorageProperties.getRemote().getPath());
        
        // TODO: Implement actual remote upload logic (e.g., FTP, S3)
        // For now, simulate success or throw error
        throw new UnsupportedOperationException("Remote upload is not yet implemented.");
    }

    @Override
    public void delete(String filePath) {
        log.info("Attempting Remote Delete: {}", filePath);
        // TODO: Implement actual remote delete logic
    }
}

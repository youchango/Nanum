package com.nanum.global.file;

import com.nanum.global.file.service.FileStorageService;
import com.nanum.global.file.service.LocalFileStorageService;
import com.nanum.global.file.service.RemoteFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FileStorageConfig {

    private final FileStorageProperties fileStorageProperties;

    @Bean
    @ConditionalOnProperty(name = "file.storage.type", havingValue = "local", matchIfMissing = true)
    public FileStorageService localFileStorageService() {
        return new LocalFileStorageService(fileStorageProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "file.storage.type", havingValue = "remote")
    public FileStorageService remoteFileStorageService() {
        return new RemoteFileStorageService(fileStorageProperties);
    }
}

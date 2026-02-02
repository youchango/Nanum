package com.nanum.global.config;

import com.nanum.global.file.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;



@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /resources/upload/** to physical path
        if ("local".equalsIgnoreCase(fileStorageProperties.getType())) {
            String pathStr = fileStorageProperties.getLocal().getPath();
            Path path = Paths.get(pathStr).toAbsolutePath().normalize();
            String location = "file:" + path.toString() + "/";

            log.info("Mapping /resources/upload/** to {}", location);

            registry.addResourceHandler("/resources/upload/**")
                    .addResourceLocations(location)
                    .setCachePeriod(3600);
        }
    }
}

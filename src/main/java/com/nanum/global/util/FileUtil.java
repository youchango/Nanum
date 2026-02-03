package com.nanum.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.ServletContextAware;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * ?�일 ?�로???�틸리티
 */
@Slf4j
@Component
@Deprecated
public class FileUtil implements ServletContextAware {

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * ?�일???�로?�합?�다.
     *
     * @param file MultipartFile 객체
     * @param subPath ?�로?�할 ?�위 경로 (?? "banner", "popup")
     * @return ?�?�된 ?�일?????�근 경로 (?? "/resources/upload/banner/filename.jpg")
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String subPath) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // ?�로???�렉?�리 경로 (webapp/resources/upload/...)
        String uploadDir = servletContext.getRealPath("/resources/upload/" + subPath);
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ?�일�??�성 (UUID + ?�본 ?�장??
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        
        String savedFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(savedFilename);

        // ?�일 ?�??
        file.transferTo(filePath.toFile());

        log.info("?�일 ?�로???�공: {}", filePath);

        // ???�근 경로 반환
        return "/resources/upload/" + subPath + "/" + savedFilename;
    }
}

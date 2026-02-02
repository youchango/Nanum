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
 * ?Œì¼ ?…ë¡œ??? í‹¸ë¦¬í‹°
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
     * ?Œì¼???…ë¡œ?œí•©?ˆë‹¤.
     *
     * @param file MultipartFile ê°ì²´
     * @param subPath ?…ë¡œ?œí•  ?˜ìœ„ ê²½ë¡œ (?? "banner", "popup")
     * @return ?€?¥ëœ ?Œì¼?????‘ê·¼ ê²½ë¡œ (?? "/resources/upload/banner/filename.jpg")
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String subPath) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // ?…ë¡œ???”ë ‰? ë¦¬ ê²½ë¡œ (webapp/resources/upload/...)
        String uploadDir = servletContext.getRealPath("/resources/upload/" + subPath);
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ?Œì¼ëª??ì„± (UUID + ?ë³¸ ?•ì¥??
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        
        String savedFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(savedFilename);

        // ?Œì¼ ?€??
        file.transferTo(filePath.toFile());

        log.info("?Œì¼ ?…ë¡œ???±ê³µ: {}", filePath);

        // ???‘ê·¼ ê²½ë¡œ ë°˜í™˜
        return "/resources/upload/" + subPath + "/" + savedFilename;
    }
}

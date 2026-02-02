package com.nanum.global.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    /**
     * ?Œì¼???€?¥ì†Œ???…ë¡œ?œí•©?ˆë‹¤.
     * @param file ?…ë¡œ?œí•  ?Œì¼
     * @param subPath ?˜ìœ„ ê²½ë¡œ (?? "banner", "popup")
     * @return ?Œì¼ ?‘ê·¼ URL ?ëŠ” ê²½ë¡œ
     * @throws IOException
     */
    String upload(MultipartFile file, String subPath) throws IOException;

    /**
     * ?Œì¼???€?¥ì†Œ?ì„œ ?? œ?©ë‹ˆ??
     * @param filePath ?? œ???Œì¼ ê²½ë¡œ
     */
    void delete(String filePath);
}

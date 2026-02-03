package com.nanum.global.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    /**
     * ?�일???�?�소???�로?�합?�다.
     * @param file ?�로?�할 ?�일
     * @param subPath ?�위 경로 (?? "banner", "popup")
     * @return ?�일 ?�근 URL ?�는 경로
     * @throws IOException
     */
    String upload(MultipartFile file, String subPath) throws IOException;

    /**
     * ?�일???�?�소?�서 ??��?�니??
     * @param filePath ??��???�일 경로
     */
    void delete(String filePath);
}

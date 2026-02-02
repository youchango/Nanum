package com.nanum.user.popup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupDTO {
    private Integer popupId;
    private String title;
    private String contentImage;
    private String contentHtml;
    private String linkType;
    private String linkUrl;
    private int width;
    private int height;
    private int posX;
    private int posY;
    private String closeType;
    private String deviceType;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDatetime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDatetime;

    private String useYn;

    private MultipartFile imageFile; // 업로드용

    // 추가 필드
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 검색용
    private String keyword;
}

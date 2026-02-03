package com.nanum.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private int contentId;
    private Integer contentType; // Changed from Enum Code ID
    private String contentTypeName; // Code Name (e.g., 공지사항)
    private String subject;
    private String contentBody;
    private String urlInfo;
    private String createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

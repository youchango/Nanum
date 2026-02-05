package com.nanum.domain.content.dto;

import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.model.ContentType;
import lombok.*;

import java.time.LocalDateTime;

public class ContentDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private ContentType type;
        private String subject;
        private String contentBody;
        private String urlInfo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private ContentType type;
        private String typeDesc;
        private String subject;
        private String contentBody;
        private String urlInfo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Content content) {
            return Response.builder()
                    .id(content.getId())
                    .type(content.getType())
                    .typeDesc(content.getType().getDescription())
                    .subject(content.getSubject())
                    .contentBody(content.getContentBody())
                    .urlInfo(content.getUrlInfo())
                    .createdAt(content.getCreatedAt())
                    .updatedAt(content.getUpdatedAt())
                    .build();
        }
    }
}

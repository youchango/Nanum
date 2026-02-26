package com.nanum.domain.product.dto;

import lombok.*;

import java.time.LocalDateTime;

public class AdminProductReviewDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long reviewId;
        private Long productId;
        private String productName;
        private String memberCode;
        private String memberName;
        private String title;
        private String content;
        private int rating;
        private int likeCount;
        private LocalDateTime createdAt;
    }
}

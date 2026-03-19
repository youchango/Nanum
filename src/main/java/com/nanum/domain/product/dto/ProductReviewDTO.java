package com.nanum.domain.product.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ProductReviewDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long orderId;
        private String title;
        private String content;
        private int rating;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long reviewId;
        private Long productId;
        private String memberCode;
        private String memberName;
        private String title;
        private String content;
        private int rating;
        private int likeCount;
        @com.fasterxml.jackson.annotation.JsonProperty("isLiked")
        private boolean isLiked; // 현재 로그인한 사용자의 좋아요 여부
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

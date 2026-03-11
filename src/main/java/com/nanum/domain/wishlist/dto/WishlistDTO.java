package com.nanum.domain.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WishlistDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long productId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long wishlistId;
        private Long productId;
        private String productName;
        private String thumbnailUrl;
        private int retailPrice;
        private int unitPrice;
    }
}

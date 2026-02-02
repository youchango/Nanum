package com.nanum.user.product.dto;

import lombok.*;

import java.util.List;

import com.nanum.user.product.model.ProductStatus;

public class ProductDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long categoryId;
        private String name;
        private int price;
        private int salePrice;
        private ProductStatus status;
        private String description;
        private String thumbnailUrl;
        private List<Option> options;
        private List<Image> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String name;
        private int extraPrice;
        private int stockQuantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Image {
        private String imageUrl;
        private String type; // MAIN, DETAIL
        private int displayOrder;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long productId;
        private String categoryName;
        private String name;
        private int price;
        private int salePrice;
        private ProductStatus status;
        private String thumbnailUrl;
    }
}

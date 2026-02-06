package com.nanum.domain.product.dto;

import lombok.*;

import java.util.List;

import com.nanum.domain.product.model.ProductStatus;

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
        private String fileId;
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
        private Long categoryId;
        private String categoryName;
        private String name;
        private int price;
        private int salePrice;
        private ProductStatus status;
        private List<com.nanum.domain.file.dto.FileResponseDTO> files;
        // options will be added separately if needed
    }
}

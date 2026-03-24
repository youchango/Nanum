package com.nanum.domain.product.dto;

import lombok.*;

import com.nanum.domain.product.model.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private List<Long> categoryIds; // Changed from single categoryId
        private String name;
        private String brandName;
        private int supplyPrice;
        private Integer mapPrice;
        private Integer retailPrice;
        private Integer suggestedPrice;
        private Integer safetyStock;
        private String optionYn;
        private ProductStatus status;
        private String applyYn;
        private String description;

        // 신규 정책 필드
        private String reviewYn;
        private String deliveryWay;
        private String deliveryArea;
        private String deliveryType;
        private String bundleShippingYn;
        private String deliveryPolicyType;
        private BigDecimal deliveryMinOrderFee;
        private String outboundShipmentCode;
        private String inboundShipmentCode;
        private BigDecimal deliveryFee;
        private BigDecimal returnFee;
        private BigDecimal exchangeFee;
        private String deliveryIslandYn;
        private BigDecimal deliveryIslandFee;

        private List<Option> options;
        private List<Image> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private Long optionId;
        private String title1;
        private String name1;
        private String title2;
        private String name2;
        private String title3;
        private String name3;
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
        private List<Long> categoryId; // Changed to List<Long> as per request
        // private Long categoryId; // Removed
        // private List<Long> categoryIds; // Removed in favor of single field
        // 'categoryId' being a List
        private String categoryName; // Representing primary or first category name for display
        private String categoryFullName;
        private String name;
        private String brandName;
        private int supplyPrice;
        private Integer mapPrice;
        private Integer retailPrice;
        private Integer suggestedPrice;
        private Integer safetyStock;
        private Integer stockQuantity;
        private ProductStatus status;
        private String applyYn;
        private String optionYn;
        private String description;

        // 신규 정책 필드
        private String reviewYn;
        private String deliveryWay;
        private String deliveryArea;
        private String deliveryType;
        private String bundleShippingYn;
        private String deliveryPolicyType;
        private BigDecimal deliveryMinOrderFee;
        private String outboundShipmentCode;
        private String inboundShipmentCode;
        private BigDecimal deliveryFee;
        private BigDecimal returnFee;
        private BigDecimal exchangeFee;
        private String deliveryIslandYn;
        private BigDecimal deliveryIslandFee;
        private BigDecimal pointRate; // 사이트별 통합된 적립률 (User 단일 사이트 기준)

        private com.nanum.domain.shipment.dto.ShipmentDTO.Response outboundShipment;
        private com.nanum.domain.shipment.dto.ShipmentDTO.Response inboundShipment;

        private List<Option> options;
        private List<Image> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MallProductResponse {
        private Long productId;
        private List<Long> categoryIds;
        private String categoryName;
        private String categoryFullName;
        private String name;
        private String brandName;
        private int supplyPrice;
        private Integer mapPrice;
        private Integer retailPrice;
        private Integer suggestedPrice;
        private Integer safetyStock;
        private Integer stockQuantity;
        private int price; // 등급별 적용된 실제 판매가
        private ProductStatus status;
        private String applyYn;
        private String optionYn;
        private String description;

        // 신규 정책 필드
        private String reviewYn;
        private String deliveryWay;
        private String deliveryArea;
        private String deliveryType;
        private String bundleShippingYn;
        private String deliveryPolicyType;
        private BigDecimal deliveryMinOrderFee;
        private String outboundShipmentCode;
        private String inboundShipmentCode;
        private BigDecimal deliveryFee;
        private BigDecimal returnFee;
        private BigDecimal exchangeFee;
        private String deliveryIslandYn;
        private BigDecimal deliveryIslandFee;
        private BigDecimal pointRate;

        private com.nanum.domain.shipment.dto.ShipmentDTO.Response outboundShipment;
        private com.nanum.domain.shipment.dto.ShipmentDTO.Response inboundShipment;

        private List<MallOptionResponse> options;
        private List<Image> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MallOptionResponse {
        private Long optionId;
        private String title1;
        private String name1;
        private String title2;
        private String name2;
        private String title3;
        private String name3;
        private int extraPrice; // 등급별 적용된 옵션 추가금
        private int stockQuantity;
    }
}

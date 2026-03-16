package com.nanum.admin.inout.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 출고용 상품 조회 결과 DTO
 */
@Getter
@Setter
public class InoutProductResponse {
    private Long productId;
    private String productName;
    private String brandName;
    private Integer stockQuantity; // 상품 가용재고 (Product)
    private Long realStock; // 상품 실재고 (ProductStock)
    private Integer safetyStock; // 상품 안전재고 (Product)
    private String productCode;
    private String status; // SALE, STOP, SOLD_OUT
    private String optionYn;
    private List<OptionResponse> options;

    @Getter
    @Setter
    public static class OptionResponse {
        private Long optionId;
        private String title1;
        private String name1;
        private String title2;
        private String name2;
        private String title3;
        private String name3;
        private Integer stockQuantity; // 옵션 가용재고 (ProductOption)
        private Long realStock; // 옵션 실재고 (ProductStock)
        private Integer safetyStock; // 상품 안전재고 (Product)
    }
}

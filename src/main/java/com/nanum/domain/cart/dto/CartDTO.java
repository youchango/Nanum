package com.nanum.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CartDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddRequest {
        private Long productId;
        private Long optionId;
        private int quantity;
        private boolean forceUpdate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private int quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long cartId;
        private Long productId;
        private Long optionId;
        private String productName;
        private String optionName;
        private int quantity;
        private String thumbnailUrl;
        private int unitPrice; // Role과 옵션 추가금액이 모두 합산된 최종 1개 단가
        private int totalPrice; // unitPrice * quantity
        private int stockQuantity; // 상품 혹은 옵션의 현재 잔여 재고
    }
}

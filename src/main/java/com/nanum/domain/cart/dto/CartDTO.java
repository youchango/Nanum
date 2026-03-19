package com.nanum.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
        @NotNull(message = "상품 ID는 필수입니다.")
        private Long productId;
        private Long optionId;
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        private int quantity;
        private boolean forceUpdate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
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

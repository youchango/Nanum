package com.nanum.domain.order.dto;

import com.nanum.domain.order.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String receiverName;
        private String receiverPhone;
        private String receiverAddress;
        private String receiverDetail;
        private String receiverZipcode;
        private String deliveryMemo;
        private List<OrderDetailItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailItem {
        private Long productId;
        private Long optionId;
        private int quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long orderId;
        private String orderName;
        private java.math.BigDecimal totalPrice;
        private OrderStatus status;
        private String receiverName;
        private java.time.LocalDateTime createdAt;
        private java.util.List<OrderDetailResponse> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailResponse {
        private Long productId;
        private String productName;
        private String optionName;
        private int quantity;
        private int pricePerUnit;
        private int totalPrice;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private OrderStatus status;
        private String trackingNumber;
    }
}

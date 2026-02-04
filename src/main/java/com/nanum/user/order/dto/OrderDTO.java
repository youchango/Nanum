package com.nanum.user.order.dto;

import com.nanum.user.order.model.OrderStatus;
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
        private String deliveryMsg;
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
        private Long totalAmount;
        private OrderStatus status;
        private String receiverName;
        private LocalDateTime createdAt;
        private List<OrderDetailResponse> items;
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

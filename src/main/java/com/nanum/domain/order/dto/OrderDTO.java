package com.nanum.domain.order.dto;

import com.nanum.domain.order.model.OrderStatus;
import com.nanum.domain.payment.model.PaymentMethod;
import com.nanum.domain.payment.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "수령인 이름은 필수입니다.")
        private String receiverName;

        @NotBlank(message = "수령인 연락처는 필수입니다.")
        private String receiverPhone;

        @NotBlank(message = "수령인 주소는 필수입니다.")
        private String receiverAddress;

        private String receiverDetail;

        @NotBlank(message = "우편번호는 필수입니다.")
        private String receiverZipcode;

        private String deliveryMemo;

        private String paymentMethod; // CARD, BANK_TRANSFER, VIRTUAL_ACCOUNT, EASY_PAY

        private Integer usedPoint; // 사용 포인트 (nullable, default 0)

        private Long memberCouponId; // 사용할 회원쿠폰 ID (nullable)

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
        @jakarta.validation.Valid
        private List<OrderDetailItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailItem {
        @jakarta.validation.constraints.NotNull(message = "상품 ID는 필수입니다.")
        private Long productId;
        private Long optionId;
        @jakarta.validation.constraints.Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        private int quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long orderId;
        private String orderNo;
        private String orderName;
        private OrderStatus status;
        private BigDecimal totalPrice;
        private BigDecimal deliveryPrice;
        private BigDecimal paymentPrice;
        private String memberCode;
        private String receiverName;
        private String receiverPhone;
        private String receiverAddress;
        private String receiverDetail;
        private String receiverZipcode;
        private LocalDateTime createdAt;
        private List<OrderDetailResponse> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResponse {
        private Long orderId;
        private String orderNo;
        private String orderName;
        private OrderStatus status;
        private BigDecimal totalPrice;
        private BigDecimal deliveryPrice;
        private BigDecimal paymentPrice;
        private String memberCode;
        private String receiverName;
        private String receiverPhone;
        private String receiverAddress;
        private String receiverDetail;
        private String receiverZipcode;
        private String deliveryMemo;
        private String trackingNumber;
        private String paymentMethod;
        private PaymentStatus paymentStatus;
        private LocalDateTime createdAt;
        private List<OrderDetailResponse> items;
        private List<PaymentResponse> payments;
        private List<DeliveryResponse> deliveries;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {
        private Long paymentId;
        private BigDecimal totalPrice;
        private BigDecimal discountPrice;
        private BigDecimal usedPoint;
        private BigDecimal usedCoupon;
        private BigDecimal paymentPrice;
        private PaymentStatus paymentStatus;
        private String paymentMethod;
        private String bankName;
        private String bankAccountNum;
        private String depositorName;
        private LocalDateTime paymentDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryResponse {
        private Long deliveryId;
        private Long orderDetailId;
        private String deliveryCorp;
        private String trackingNumber;
        private String status; // DeliveryStatus name
        private LocalDateTime shippedAt;
        private LocalDateTime completedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailResponse {
        private Long orderDetailId;
        private Long productId;
        private String productName;
        private String optionName;
        private int quantity;
        private BigDecimal pricePerUnit;
        private BigDecimal totalPrice;
        @com.fasterxml.jackson.annotation.JsonProperty("reviewYn")
        private boolean reviewYn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private OrderStatus status;
        private String trackingNumber;
    }

    // --- PG 연동용 DTO ---

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrepareRequest {
        @NotBlank(message = "수령인 이름은 필수입니다.")
        private String receiverName;

        @NotBlank(message = "수령인 연락처는 필수입니다.")
        private String receiverPhone;

        @NotBlank(message = "수령인 주소는 필수입니다.")
        private String receiverAddress;

        private String receiverDetail;

        @NotBlank(message = "우편번호는 필수입니다.")
        private String receiverZipcode;

        private String deliveryMemo;

        private String paymentMethod;

        private Integer usedPoint;

        private Long memberCouponId;

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
        @jakarta.validation.Valid
        private List<OrderDetailItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrepareResponse {
        private String orderNo;
        private String orderName;
        private BigDecimal totalPrice;
        private BigDecimal deliveryPrice;
        private BigDecimal paymentPrice;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfirmRequest {
        @NotBlank(message = "주문번호는 필수입니다.")
        private String orderNo;

        @NotBlank(message = "결제키는 필수입니다.")
        private String paymentKey;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryRegisterRequest {
        private Long orderDetailId;
        private String deliveryCorp;
        private String trackingNumber;
    }
}

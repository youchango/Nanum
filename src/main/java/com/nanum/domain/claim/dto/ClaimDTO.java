package com.nanum.domain.claim.dto;

import com.nanum.domain.claim.model.Claim;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClaimDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotNull(message = "주문 ID는 필수입니다.")
        private Long orderId;

        /** null이면 전체 주문 대상 클레임 */
        private Long orderDetailId;

        @NotBlank(message = "클레임 유형은 필수입니다. (EXCHANGE/RETURN/REFUND)")
        private String claimType;

        @NotBlank(message = "클레임 사유는 필수입니다.")
        private String claimReason;

        @NotBlank(message = "상세 사유는 필수입니다.")
        @Size(min = 10, message = "상세 사유는 최소 10자 이상 입력해주세요.")
        private String claimReasonDetail;

        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        private int quantity;

        /** 현금 환불 시 은행명 (관리자가 후처리 가능) */
        private String refundBankName;
        private String refundAccountNum;
        private String refundAccountName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long claimId;
        private Long orderId;
        private String orderNo;
        private String orderName;
        private String claimType;
        private String claimStatus;
        private String claimReason;
        private String claimReasonDetail;
        private String productName;
        private int quantity;
        private java.util.List<OrderItem> orderItems; // 주문 전체 클레임 시 전체 상품 목록
        private String refundType;
        private BigDecimal refundPrice;
        private LocalDateTime requestedAt;
        private LocalDateTime reviewedAt;
        private LocalDateTime completedAt;
        private String adminMemo;

        public static Response from(Claim claim) {
            return Response.builder()
                    .claimId(claim.getClaimId())
                    .orderId(claim.getOrderMaster().getOrderId())
                    .orderNo(claim.getOrderMaster().getOrderNo())
                    .orderName(claim.getOrderMaster().getOrderName())
                    .claimType(claim.getClaimType())
                    .claimStatus(claim.getClaimStatus())
                    .claimReason(claim.getClaimReason())
                    .claimReasonDetail(claim.getClaimReasonDetail())
                    .productName(claim.getProductName())
                    .quantity(claim.getQuantity())
                    .refundType(claim.getRefundType())
                    .refundPrice(claim.getRefundPrice())
                    .requestedAt(claim.getRequestedAt())
                    .reviewedAt(claim.getReviewedAt())
                    .completedAt(claim.getCompletedAt())
                    .adminMemo(claim.getAdminMemo())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        private String productName;
        private String optionName;
        private int quantity;
        private int pricePerUnit;
    }
}

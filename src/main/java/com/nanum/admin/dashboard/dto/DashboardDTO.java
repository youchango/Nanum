package com.nanum.admin.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
public class DashboardDTO {

    @Schema(description = "최근 주문 내역")
    private List<OrderSummary> recentOrders;

    @Schema(description = "최근 교환/반품 신청 현황")
    private List<ClaimSummary> recentClaims;

    @Schema(description = "최근 결제 내역")
    private List<PaymentSummary> recentPayments;

    @Schema(description = "최근 포인트 내역")
    private List<PointSummary> recentPoints;

    @Schema(description = "최근 1:1 문의 내역")
    private List<InquirySummary> recentInquiries;

    @Schema(description = "최근 배송 내역")
    private List<DeliverySummary> recentDeliveries;

    @Getter
    @Builder
    @ToString
    public static class OrderSummary {
        @Schema(description = "주문 번호")
        private String orderNo;

        @Schema(description = "상품명")
        private String productName;

        @Schema(description = "주문자명")
        private String ordererName;

        @Schema(description = "주문 상태")
        private String orderStatus;

        @Schema(description = "주문 일시")
        private LocalDateTime orderDate;
    }

    @Getter
    @Builder
    @ToString
    public static class ClaimSummary {
        @Schema(description = "클레임 번호")
        private Long claimId;

        @Schema(description = "신청 사유")
        private String reason;

        @Schema(description = "진행 상태")
        private String status;

        @Schema(description = "신청 일시")
        private LocalDateTime requestDate;
    }

    @Getter
    @Builder
    @ToString
    public static class PaymentSummary {
        @Schema(description = "결제 번호")
        private String paymentNo;

        @Schema(description = "결제 수단")
        private String paymentMethod;

        @Schema(description = "결제 금액")
        private Long finalPayPrice;

        @Schema(description = "결제 일시")
        private LocalDateTime paymentDate;
    }

    @Getter
    @Builder
    @ToString
    public static class PointSummary {
        @Schema(description = "사용자 코드")
        private String memberCode;

        @Schema(description = "적립/차감 포인트")
        private Long point;

        @Schema(description = "처리 유형 (적립/사용)")
        private String type;

        @Schema(description = "처리 일시")
        private LocalDateTime processDate;
    }

    @Getter
    @Builder
    @ToString
    public static class InquirySummary {
        @Schema(description = "문의 번호")
        private Long inquiryId;

        @Schema(description = "제목")
        private String title;

        @Schema(description = "작성자")
        private String writerName;

        @Schema(description = "답변 여부")
        private String answerYn;

        @Schema(description = "등록 일시")
        private LocalDateTime regDate;
    }

    @Getter
    @Builder
    @ToString
    public static class DeliverySummary {
        @Schema(description = "송장 번호")
        private String trackingNo;

        @Schema(description = "배송 상태")
        private String status;

        @Schema(description = "택배사")
        private String courier;

        @Schema(description = "발송 일시")
        private LocalDateTime deliveryDate;
    }
}

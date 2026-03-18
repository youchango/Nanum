package com.nanum.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

public class TaxBillDTO {

    // ==================== 사업자 정보 저장/조회 ====================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaxInfoRequest {
        private String bizRegNum;
        private String bizName;
        private String bizRepName;
        private String bizCategory;
        private String bizDetailCategory;
        private String bizAddress;
        private String bizEmail;
        private String bizMobile;
        private String damName;

        /** 현금영수증 발행 용도: INCOME(소득공제), EXPENSE(지출증빙) */
        private String receiptPurpose;
        /** 현금영수증 식별번호 (휴대폰번호 또는 사업자번호) */
        private String receiptIdNum;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaxInfoResponse {
        private Long id;
        private String infoType;
        private String bizRegNum;
        private String bizName;
        private String bizRepName;
        private String bizCategory;
        private String bizDetailCategory;
        private String bizAddress;
        private String bizEmail;
        private String bizMobile;
        private String damName;
        private String receiptPurpose;
        private String receiptIdNum;
    }

    // ==================== 발행 신청 ====================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyRequest {
        private Long orderId;

        @NotBlank(message = "발행 유형은 필수입니다. (TAX_BILL / CASH_RECEIPT)")
        private String billType;

        // 세금계산서 전용
        private String bizRegNum;
        private String bizName;
        private String bizRepName;
        private String bizCategory;
        private String bizDetailCategory;
        private String bizAddress;
        private String bizEmail;
        private String bizMobile;

        // 현금영수증 전용
        private String receiptPurpose;
        private String receiptIdNum;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyResponse {
        private Long id;
        private String orderNo;
        private Long orderId;
        private String billType;
        private String status;

        // 세금계산서
        private String bizRegNum;
        private String bizName;

        // 현금영수증
        private String receiptIdNum;

        private LocalDateTime issueDate;
        private String ntsConfirmNum;
        private LocalDateTime createdAt;
    }
}

package com.nanum.domain.payment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("대기"),
    DEPOSIT_WAIT("입금대기"),
    PAID("결제완료"),
    CANCELLED("결제취소"),
    FAILED("결제실패"),
    REFUND_REQUESTED("환불신청"),
    REFUNDED("환불완료");

    private final String description;
}

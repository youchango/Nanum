package com.nanum.domain.payment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("대기"),
    PAID("결제완료"),
    CANCELLED("취소");

    private final String description;
}

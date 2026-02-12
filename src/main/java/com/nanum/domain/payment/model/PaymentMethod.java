package com.nanum.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드결제"),
    BANK_TRANSFER("실시간계좌이체"),
    VIRTUAL_ACCOUNT("가상계좌"),
    MOBILE("휴대폰결제"),
    EASY_PAY("간편결제");

    private final String description;
}

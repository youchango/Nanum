package com.nanum.domain.delivery.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    READY("배송준비"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    RETURN_REQUESTED("반품신청"),
    RETURNING("반품회수중"),
    RETURN_COMPLETED("반품완료"),
    EXCHANGE_REQUESTED("교환신청"),
    EXCHANGING("교환회수중"),
    EXCHANGE_COMPLETED("교환완료");

    private final String description;
}

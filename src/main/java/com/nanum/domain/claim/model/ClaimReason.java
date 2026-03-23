package com.nanum.domain.claim.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimReason {
    ORDER_ERROR("주문오류"),
    CHANGE_OF_MIND("단순변심"),
    DEFECTIVE("상품불량"),
    DAMAGED("파손"),
    MISDELIVERY("오배송"),
    OTHER("기타");

    private final String description;
}

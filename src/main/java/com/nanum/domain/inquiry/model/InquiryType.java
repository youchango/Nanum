package com.nanum.domain.inquiry.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryType {
    PRODUCT("상품문의", 1),
    DELIVERY("배송문의", 2),
    ORDER("주문/결제", 3),
    ETC("기타", 99);

    private final String description;
    private final int code; // DB ID mapping if needed, or simple enum ordinal/string
}

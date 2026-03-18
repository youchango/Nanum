package com.nanum.domain.inquiry.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryType {
    PRODUCT("상품"),
    DELIVERY("배송"),
    ORDER("주문"),
    ETC("기타");

    private final String description;
}

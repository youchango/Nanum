package com.nanum.user.inquiry.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {
    PENDING("대기"),
    ANSWERED("답변완료");

    private final String description;
}

package com.nanum.domain.inquiry.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {
    WAIT("접수"),
    PROCESS("처리중"),
    COMPLETE("답변완료");

    private final String description;
}

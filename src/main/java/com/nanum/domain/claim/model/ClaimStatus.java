package com.nanum.domain.claim.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimStatus {
    REQUESTED("접수"),
    REVIEWING("검토중"),
    APPROVED("승인"),
    REJECTED("반려"),
    COMPLETED("완료");

    private final String description;
}

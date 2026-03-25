package com.nanum.domain.coupon.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 쿠폰 상태 Enum
 */
@Getter
@RequiredArgsConstructor
public enum MemberCouponStatus {
    UNUSED("미사용"),
    USED("사용완료"),
    EXPIRED("만료");

    private final String description;
}

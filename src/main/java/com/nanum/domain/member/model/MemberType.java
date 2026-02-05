package com.nanum.domain.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    M("관리자"),
    B("업무자"),
    U("사용자");

    private final String description;
}

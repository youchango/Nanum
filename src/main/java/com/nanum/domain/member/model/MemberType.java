package com.nanum.domain.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    U("일반 회원"),
    B("기업 회원"),
    V("보훈 회원");

    private final String description;
}

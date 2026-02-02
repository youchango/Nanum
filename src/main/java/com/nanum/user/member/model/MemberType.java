package com.nanum.user.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    ADMIN("관리자"),
    BIZ("업무자"),
    USER("사용자");

    private final String description;
}

package com.nanum.user.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    ROLE_MASTER("마스터"),
    ROLE_BIZ("업무자"),
    ROLE_USER("사용자");

    private final String description;
}

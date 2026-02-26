package com.nanum.domain.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    ROLE_MASTER("마스터"),
    ROLE_ADMIN("관리자"),
    ROLE_SCM("매니저"),
    ROLE_BIZ("기업회원"),
    ROLE_USER("일반회원"),
    ROLE_VETERAN("보훈회원");

    private final String description;
}

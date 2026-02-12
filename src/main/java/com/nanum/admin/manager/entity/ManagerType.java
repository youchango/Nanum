package com.nanum.admin.manager.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagerType {
    MASTER("마스터"),
    ADMIN("관리자"),
    SCM("입점사");

    private final String description;
}

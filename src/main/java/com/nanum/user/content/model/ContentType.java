package com.nanum.user.content.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    PRODUCT("제품"),
    SERVICE("서비스"),
    NOTICE("공지사항"),
    FAQ("자주 묻는 질문");

    private final String description;
}

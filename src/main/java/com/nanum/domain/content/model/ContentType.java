package com.nanum.domain.content.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    NOTICE("공지사항", 1),
    FAQ("FAQ", 2);

    private final String description;
    private final int code;
}

package com.nanum.domain.content.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    NOTICE("공지사항"),
    FAQ("FAQ");

    private final String description;
}

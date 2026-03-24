package com.nanum.domain.banner.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BannerType {
    MAIN_TOP("메인 상단"),
    MAIN_MID("메인 중간"),
    MAIN_BOTTOM("메인 하단"),
    SUB_LEFT("서브 좌측"),
    SUB_RIGHT("서브 우측");

    private final String description;
}

package com.nanum.domain.banner.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BannerType {
    MAIN_TOP("메인 상단"),
    SUB_MID("서브 중간");

    private final String description;
}

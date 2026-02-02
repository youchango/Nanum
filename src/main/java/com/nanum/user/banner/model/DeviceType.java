package com.nanum.user.banner.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
    PC("PC"),
    MOBILE("모바일"),
    ALL("전체");

    private final String description;
}

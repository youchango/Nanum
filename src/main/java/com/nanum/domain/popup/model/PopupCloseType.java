package com.nanum.domain.popup.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PopupCloseType {
    NEVER("오늘 하루 열지 않음 없음"), // Or just standard close
    DAY("오늘 하루 열지 않음"),
    ONCE("다시 보지 않음");

    private final String description;
}

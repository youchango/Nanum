package com.nanum.domain.point.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포인트 구분 Enum
 * SAVE: 적립
 * USE: 사용
 */
@Getter
@RequiredArgsConstructor
public enum PointType {
    SAVE("적립"),
    USE("사용");

    private final String description;
}

package com.nanum.domain.file.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReferenceType {
    PRODUCT("상품"),
    CATEGORY("카테고리"),
    BANNER("배너"),
    POPUP("팝업"),
    INQUIRY("문의"),
    REVIEW("리뷰");

    private final String description;
}

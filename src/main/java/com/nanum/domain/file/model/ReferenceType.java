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
    REVIEW("리뷰"),
    SCM("SCM/사업자"),
    BIZ("사업자등록증"),
    EDITOR("에디터");

    private final String description;
}

package com.nanum.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProductReviewSearchDTO {
    private String searchType; // ALL, PRODUCT_NAME, MEMBER_CODE, TITLE
    private String searchKeyword;
    private Integer rating; // 1~5 별점 필터
}

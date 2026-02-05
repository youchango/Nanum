package com.nanum.domain.product.dto;

import com.nanum.domain.product.model.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminProductSearchDTO {
    private Long categoryId;
    private String searchType; // "NAME", "CODE"
    private String searchKeyword;
    private ProductStatus status; // SALE, STOP, SOLD_OUT
    private String startDate; // yyyy-MM-dd
    private String endDate; // yyyy-MM-dd
}

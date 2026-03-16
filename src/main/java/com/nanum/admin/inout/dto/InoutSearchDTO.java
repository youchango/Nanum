package com.nanum.admin.inout.dto;

import com.nanum.global.common.dto.SearchDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 입출고 검색 DTO
 */
@Getter
@Setter
public class InoutSearchDTO extends SearchDTO {
    private String searchType; // io_code, product_name, brand_name
    private String searchKeyword;
    private String ioType; // 입출고구분(IN/OUT)
    private String ioCategory; // 입출고 카테고리 (일반입고, 생산입고, 반품입고, 일반출고, 생산출고, 주문출고, 폐기출고)
}

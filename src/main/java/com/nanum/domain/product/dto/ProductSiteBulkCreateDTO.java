package com.nanum.domain.product.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductSiteBulkCreateDTO {
    private List<String> siteCds;

    // 이 값들을 바탕으로 상품과 1:N인 product_site의 가격 데이터 세팅을 진행합니다.
    private BigDecimal aPrice;
    private BigDecimal bPrice;
    private BigDecimal cPrice;
}

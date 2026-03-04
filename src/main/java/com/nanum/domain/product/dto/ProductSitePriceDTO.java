package com.nanum.domain.product.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductSitePriceDTO {
    private Long optionId;
    private String optionName;
    private Integer standardPrice;
    private BigDecimal aPrice;
    private BigDecimal bPrice;
    private BigDecimal cPrice;
    private BigDecimal aExtraPrice;
    private BigDecimal bExtraPrice;
    private BigDecimal cExtraPrice;
}

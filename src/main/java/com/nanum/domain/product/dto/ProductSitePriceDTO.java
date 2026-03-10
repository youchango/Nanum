package com.nanum.domain.product.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductSitePriceDTO {
    private String siteCd;
    private Long optionId;
    private String optionName1;
    private String optionName2;
    private String optionName3;
    @JsonProperty("viewYn")
    private String viewYn;
    @JsonProperty("salePrice")
    private Integer salePrice;
    @JsonProperty("aPrice")
    private BigDecimal aPrice;
    @JsonProperty("bPrice")
    private BigDecimal bPrice;
    @JsonProperty("cPrice")
    private BigDecimal cPrice;
    @JsonProperty("extraPrice")
    private Integer extraPrice;
    @JsonProperty("stockQuantity")
    private Integer stockQuantity;
}

package com.nanum.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Schema(description = "사이트별 가격 설정 목록")
    private List<SitePriceReq> sitePrices;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class SitePriceReq {
        @Schema(description = "상점 코드", example = "A01")
        private String siteCd;

        @Schema(description = "기업회원가(A)")
        @JsonProperty("aPrice")
        private BigDecimal aPrice;

        @Schema(description = "일반회원가(B)")
        @JsonProperty("bPrice")
        private BigDecimal bPrice;

        @Schema(description = "보훈회원가(C)")
        @JsonProperty("cPrice")
        private BigDecimal cPrice;
    }
}

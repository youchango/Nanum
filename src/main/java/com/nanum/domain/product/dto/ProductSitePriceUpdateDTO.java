package com.nanum.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 관리자: 사이트별 상품 가격 및 노출 여부 일괄 업데이트 요청 DTO
 * Jackson이 @RequestBody 역직렬화를 위해 public 기본 생성자(NoArgsConstructor) + @Setter 필요
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "관리자: 사이트별 상품 및 옵션 가격 일괄 업데이트 요청 DTO")
public class ProductSitePriceUpdateDTO {

    @Schema(description = "쇼핑몰 노출 여부 (Y/N)", example = "Y")
    private String viewYn;

    @Schema(description = "기업회원가 (A)", example = "10000.0000")
    @JsonProperty("aPrice")
    private BigDecimal aPrice;

    @Schema(description = "일반회원가 (B)", example = "12000.0000")
    @JsonProperty("bPrice")
    private BigDecimal bPrice;

    @Schema(description = "보훈회원가 (C)", example = "11000.0000")
    @JsonProperty("cPrice")
    private BigDecimal cPrice;

}

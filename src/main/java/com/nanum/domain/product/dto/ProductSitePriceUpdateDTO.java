package com.nanum.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

    @Schema(description = "단일 사이트 옵션 가격 목록")
    private List<OptionPriceUpdate> dtoList;

    /**
     * 옵션별 추가 금액 수정 요청 내부 클래스
     * Jackson 역직렬화를 위해 public NoArgsConstructor + @Setter 필요
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "옵션별 추가 금액 수정 요청")
    public static class OptionPriceUpdate {

        @Schema(description = "수정할 상품 옵션 ID", example = "10")
        private Long optionId;

        @Schema(description = "옵션 기업회원 추가금액 (A)", example = "500.0000")
        @JsonProperty("aExtraPrice")
        private BigDecimal aExtraPrice;

        @Schema(description = "옵션 일반회원 추가금액 (B)", example = "500.0000")
        @JsonProperty("bExtraPrice")
        private BigDecimal bExtraPrice;

        @Schema(description = "옵션 보훈회원 추가금액 (C)", example = "500.0000")
        @JsonProperty("cExtraPrice")
        private BigDecimal cExtraPrice;
    }
}

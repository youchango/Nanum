package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "product_option_site")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductOptionSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_id")
    private Long posId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ps_id", nullable = false)
    private ProductSite productSite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private ProductOption productOption;

    @Column(name = "a_extra_price", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal aExtraPrice;

    @Column(name = "b_extra_price", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal bExtraPrice;

    @Column(name = "c_extra_price", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal cExtraPrice;

    /**
     * 옵션 사이트별 추가 금액을 업데이트합니다.
     * null이 전달된 경우 기존 값을 유지합니다.
     *
     * @param aExtraPrice 기업회원 옵션 추가금액, null이면 기존 값 유지
     * @param bExtraPrice 일반회원 옵션 추가금액, null이면 기존 값 유지
     * @param cExtraPrice 보훈회원 옵션 추가금액, null이면 기존 값 유지
     */
    public void updateExtraPrices(BigDecimal aExtraPrice, BigDecimal bExtraPrice, BigDecimal cExtraPrice) {
        if (aExtraPrice != null)
            this.aExtraPrice = aExtraPrice;
        if (bExtraPrice != null)
            this.bExtraPrice = bExtraPrice;
        if (cExtraPrice != null)
            this.cExtraPrice = cExtraPrice;
    }
}

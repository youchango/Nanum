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

    public void updateExtraPrices(BigDecimal aExtraPrice, BigDecimal bExtraPrice, BigDecimal cExtraPrice) {
        this.aExtraPrice = aExtraPrice;
        this.bExtraPrice = bExtraPrice;
        this.cExtraPrice = cExtraPrice;
    }
}

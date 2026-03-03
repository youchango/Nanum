package com.nanum.domain.product.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;

@Entity
@Table(name = "product_site")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductSite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ps_id")
    private Long psId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_id", nullable = false)
    private Long optionId; // Assuming Option entity or just ID. SQL says FK to product_option.
    // Ideally should be @ManyToOne to ProductOption if it exists.
    // Since I didn't verify ProductOption entity existence in detail (saw
    // ProductOption table in SQL),
    // I check if ProductOption java entity exists.
    // List of product dir had 20 children.

    @Column(name = "site_cd", length = 100)
    private String siteCd;

    @Column(name = "view_yn", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @Builder.Default
    private String viewYn = "N";

    @Column(name = "standard_price", nullable = true)
    @ColumnDefault("0")
    private Integer standardPrice;

    @Column(name = "a_price", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal aPrice;

    @Column(name = "b_price", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal bPrice;

    @Column(name = "c_price", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal cPrice;

    @Column(name = "pdt_click", nullable = false)
    @ColumnDefault("0")
    private Integer pdtClick;

    public void update(String viewYn, Integer standardPrice, BigDecimal aPrice, BigDecimal bPrice, BigDecimal cPrice) {
        this.viewYn = viewYn;
        this.standardPrice = standardPrice;
        this.aPrice = aPrice;
        this.bPrice = bPrice;
        this.cPrice = cPrice;
    }
}

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

    @Column(name = "site_cd", length = 100)
    private String siteCd;

    @Column(name = "view_yn", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @Builder.Default
    private String viewYn = "N";

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

    /**
     * 사이트별 가격 및 노출 여부를 업데이트합니다.
     * null이 전달된 경우 기존 값을 유지합니다.
     *
     * @param viewYn 노출 여부 (Y/N), null이면 기존 값 유지
     * @param aPrice 기업회원가, null이면 기존 값 유지
     * @param bPrice 일반회원가, null이면 기존 값 유지
     * @param cPrice 보훈회원가, null이면 기존 값 유지
     */
    public void update(String viewYn, BigDecimal aPrice, BigDecimal bPrice, BigDecimal cPrice) {
        if (viewYn != null)
            this.viewYn = viewYn;
        if (aPrice != null)
            this.aPrice = aPrice;
        if (bPrice != null)
            this.bPrice = bPrice;
        if (cPrice != null)
            this.cPrice = cPrice;
    }
}

package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_name", nullable = false)
    private String name;

    @Column(name = "extra_price", nullable = false)
    @ColumnDefault("0")
    private int extraPrice;

    @Column(name = "stock_quantity", nullable = false)
    @ColumnDefault("0")
    private int stockQuantity; // 사용자 노출용 재고 (Display Stock)

    @Column(name = "use_yn", nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;
}

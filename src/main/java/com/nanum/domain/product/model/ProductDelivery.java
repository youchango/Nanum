package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pd_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "min_quantity", nullable = false)
    private int minQuantity;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    public void update(int minQuantity, Integer maxQuantity, BigDecimal deliveryFee) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.deliveryFee = deliveryFee;
    }
}

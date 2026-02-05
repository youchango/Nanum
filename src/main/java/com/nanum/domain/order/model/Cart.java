package com.nanum.domain.order.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @Column(name = "member_code", nullable = false)
    private String memberCode;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_id")
    private Long optionId;

    @Column(nullable = false)
    @ColumnDefault("1")
    private int quantity;

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}

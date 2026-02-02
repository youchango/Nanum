package com.nanum.user.order.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderMaster order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_id")
    private Long optionId;

    // Snapshot Data
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "option_name")
    private String optionName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "sub_total", nullable = false)
    private int subTotal;

    public void setOrder(OrderMaster order) {
        this.order = order;
    }
}

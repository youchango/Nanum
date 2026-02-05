package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_history_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InventoryHistoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private InventoryHistoryMaster master;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private ProductOption option;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InventoryType type;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "prev_quantity", nullable = false)
    private int prevQuantity;

    @Column(name = "curr_quantity", nullable = false)
    private int currQuantity;

    @Column(name = "memo")
    private String memo;

    public void setMaster(InventoryHistoryMaster master) {
        this.master = master;
    }
}

package com.nanum.user.product.model;

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
    private int stockQuantity;

    @Column(name = "use_yn", nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;

    public void setProduct(Product product) {
        this.product = product;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalArgumentException("need more stock");
        }
        this.stockQuantity = restStock;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
}

package com.nanum.user.product.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product SET delete_yn = 'Y', deleted_at = NOW() WHERE product_id = ?")
@Where(clause = "delete_yn = 'N'")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int price;

    @Column(name = "sale_price")
    @ColumnDefault("0")
    private int salePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.SALE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "view_count")
    @ColumnDefault("0")
    private int viewCount;

    @Column(name = "delete_yn", nullable = false)
    @ColumnDefault("'N'")
    private String deleteYn;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    // Business Methods
    public void update(String name, int price, int salePrice, String description, String thumbnailUrl,
            ProductStatus status) {
        this.name = name;
        this.price = price;
        this.salePrice = salePrice;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
    }

    public void changeCategory(ProductCategory category) {
        this.category = category;
    }
}

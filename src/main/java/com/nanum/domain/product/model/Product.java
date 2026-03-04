package com.nanum.domain.product.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product SET delete_yn = 'Y', deleted_at = NOW() WHERE product_id = ?")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "product_category_by", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private List<ProductCategory> categories = new ArrayList<>();

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "brand_name", length = 100)
    private String brandName;

    @Column(name = "supply_price", nullable = false)
    @ColumnDefault("0")
    private int supplyPrice;

    @Column(name = "map_price", nullable = false)
    @ColumnDefault("0")
    private int mapPrice;

    @Column(name = "standard_price")
    @ColumnDefault("0")
    private int standardPrice;

    @Column(name = "option_yn", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @Builder.Default
    private String optionYn = "N";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.SALE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "view_count")
    @ColumnDefault("0")
    private int viewCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    // Business Methods
    public void update(String name, int mapPrice, int standardPrice, String description,
            ProductStatus status) {
        this.name = name;
        this.mapPrice = mapPrice;
        this.standardPrice = standardPrice;
        this.description = description;
        this.status = status;
    }

    public void updateInfo(List<ProductCategory> categories, String name, int mapPrice, int standardPrice,
            String optionYn, ProductStatus status, String description) {
        this.categories = categories;
        this.name = name;
        this.mapPrice = mapPrice;
        this.standardPrice = standardPrice;
        this.optionYn = optionYn;
        this.status = status;
        this.description = description;
    }
}

package com.nanum.domain.product.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

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

    @Column(name = "map_price", nullable = true)
    @ColumnDefault("0")
    private Integer mapPrice;

    @Column(name = "retail_price")
    @ColumnDefault("0")
    private Integer retailPrice;

    @Column(name = "suggested_price")
    @ColumnDefault("0")
    private Integer suggestedPrice;

    @Column(name = "option_yn", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @Builder.Default
    private String optionYn = "N";

    @Column(name = "stock_quantity", nullable = false, insertable = false, updatable = false)
    @ColumnDefault("0")
    private Integer stockQuantity;

    @Column(name = "safety_stock")
    @ColumnDefault("0")
    private Integer safetyStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.SALE;

    @Column(name = "apply_yn", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @Builder.Default
    private String applyYn = "N";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "view_count")
    @ColumnDefault("0")
    private int viewCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    // Business Methods
    public void update(String name, String brandName, int supplyPrice, Integer mapPrice, Integer retailPrice,
            Integer suggestedPrice, Integer safetyStock,
            String description,
            ProductStatus status, String applyYn) {
        this.name = name;
        this.brandName = brandName;
        this.supplyPrice = supplyPrice;
        this.mapPrice = mapPrice;
        this.retailPrice = retailPrice;
        this.suggestedPrice = suggestedPrice;
        this.safetyStock = safetyStock;
        this.description = description;
        this.status = status;
        this.applyYn = applyYn != null ? applyYn : "N";
    }

    public void updateInfo(List<ProductCategory> categories, String name, String brandName, int supplyPrice,
            Integer mapPrice,
            Integer retailPrice,
            Integer suggestedPrice,
            Integer safetyStock,
            String optionYn, ProductStatus status, String description, String applyYn) {
        this.categories = categories;
        this.name = name;
        this.brandName = brandName;
        this.supplyPrice = supplyPrice;
        this.mapPrice = mapPrice;
        this.retailPrice = retailPrice;
        this.suggestedPrice = suggestedPrice;
        this.safetyStock = safetyStock;
        this.optionYn = optionYn;
        this.status = status;
        this.description = description;
        this.applyYn = applyYn != null ? applyYn : "N";
    }
}

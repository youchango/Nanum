package com.nanum.domain.product.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
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

    @Column(name = "standard_price")
    @ColumnDefault("0")
    private Integer standardPrice;

    @Column(name = "min_order_amount", precision = 19, scale = 4, nullable = false)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(name = "review_yn", nullable = false, length = 1)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String reviewYn = "Y";

    @Column(name = "view_count")
    @ColumnDefault("0")
    private int viewCount;

    // === 배송 관련 ===
    @Column(name = "delivery_way", length = 20)
    private String deliveryWay;

    @Column(name = "delivery_area", length = 500)
    private String deliveryArea;

    @Column(name = "delivery_type", length = 20)
    private String deliveryType; // FREE, PAY, COND

    @Column(name = "bundle_shipping_yn", nullable = false, length = 1)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String bundleShippingYn = "Y";

    @Column(name = "delivery_policy_type", nullable = false, length = 20)
    @ColumnDefault("'MAX'")
    @Builder.Default
    private String deliveryPolicyType = "MAX"; // MAX or MIN

    @Column(name = "delivery_min_order_fee", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal deliveryMinOrderFee = BigDecimal.ZERO;

    @Column(name = "outbound_shipment_code", length = 30)
    private String outboundShipmentCode;

    @Column(name = "inbound_shipment_code", length = 30)
    private String inboundShipmentCode;

    @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "return_fee", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal returnFee = BigDecimal.ZERO;

    @Column(name = "exchange_fee", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal exchangeFee = BigDecimal.ZERO;

    @Column(name = "delivery_island_yn", nullable = false, length = 1)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String deliveryIslandYn = "Y";

    @Column(name = "delivery_island_fee", nullable = false, precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal deliveryIslandFee = BigDecimal.ZERO;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    // Business Methods
    public void update(String name, String brandName, int supplyPrice, Integer mapPrice, Integer retailPrice,
            Integer suggestedPrice, Integer safetyStock,
            String description,
            ProductStatus status, String applyYn,
            String reviewYn, String deliveryWay, String deliveryArea, String deliveryType, String bundleShippingYn,
            String deliveryPolicyType, BigDecimal deliveryMinOrderFee, String outboundShipmentCode, String inboundShipmentCode,
            BigDecimal deliveryFee, BigDecimal returnFee, BigDecimal exchangeFee, String deliveryIslandYn, BigDecimal deliveryIslandFee) {
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
        this.reviewYn = reviewYn;
        this.deliveryWay = deliveryWay;
        this.deliveryArea = deliveryArea;
        this.deliveryType = deliveryType;
        this.bundleShippingYn = bundleShippingYn;
        this.deliveryPolicyType = deliveryPolicyType;
        this.deliveryMinOrderFee = deliveryMinOrderFee;
        this.outboundShipmentCode = outboundShipmentCode;
        this.inboundShipmentCode = inboundShipmentCode;
        this.deliveryFee = deliveryFee;
        this.returnFee = returnFee;
        this.exchangeFee = exchangeFee;
        this.deliveryIslandYn = deliveryIslandYn;
        this.deliveryIslandFee = deliveryIslandFee;
    }

    public void updateInfo(List<ProductCategory> categories, String name, String brandName, int supplyPrice,
            Integer mapPrice,
            Integer retailPrice,
            Integer suggestedPrice,
            Integer safetyStock,
            String optionYn, ProductStatus status, String description, String applyYn,
            String reviewYn, String deliveryWay, String deliveryArea, String deliveryType, String bundleShippingYn,
            String deliveryPolicyType, BigDecimal deliveryMinOrderFee, String outboundShipmentCode, String inboundShipmentCode,
            BigDecimal deliveryFee, BigDecimal returnFee, BigDecimal exchangeFee, String deliveryIslandYn, BigDecimal deliveryIslandFee) {
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
        this.reviewYn = reviewYn;
        this.deliveryWay = deliveryWay;
        this.deliveryArea = deliveryArea;
        this.deliveryType = deliveryType;
        this.bundleShippingYn = bundleShippingYn;
        this.deliveryPolicyType = deliveryPolicyType;
        this.deliveryMinOrderFee = deliveryMinOrderFee;
        this.outboundShipmentCode = outboundShipmentCode;
        this.inboundShipmentCode = inboundShipmentCode;
        this.deliveryFee = deliveryFee;
        this.returnFee = returnFee;
        this.exchangeFee = exchangeFee;
        this.deliveryIslandYn = deliveryIslandYn;
        this.deliveryIslandFee = deliveryIslandFee;
    }

    /**
     * 재고 수량을 업데이트합니다.
     * @param stockQuantity 새로운 재고 수량
     */
    public void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}

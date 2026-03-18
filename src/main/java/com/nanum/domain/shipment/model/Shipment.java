package com.nanum.domain.shipment.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

/**
 * 출고지/입고지 및 배송 정책 엔티티
 */
@Entity
@Table(name = "shipment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE shipment SET delete_yn = 'Y', deleted_at = NOW() WHERE shipment_id = ?")
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    @Column(name = "manager_code", nullable = false, length = 30)
    private String managerCode;

    @Column(name = "shipment_code", nullable = false, unique = true, length = 50)
    private String shipmentCode;

    @Column(name = "shipment_type", nullable = false, length = 20)
    @Builder.Default
    private String shipmentType = "OUT"; // IN(입고) / OUT(출고)

    @Column(name = "shipment_name", nullable = false, length = 100)
    private String shipmentName;

    @Column(name = "zipcode", nullable = false, length = 10)
    private String zipcode;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "address_detail", nullable = false, length = 200)
    private String addressDetail;

    @Column(name = "supplier_name", nullable = false, length = 50)
    private String supplierName;

    @Column(name = "phone", nullable = false, length = 45)
    private String phone;

    @Column(name = "mobile", length = 45)
    private String mobile;

    @Column(name = "shipping_fee", precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "return_fee", precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal returnFee = BigDecimal.ZERO;

    @Column(name = "exchange_fee", precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal exchangeFee = BigDecimal.ZERO;

    @Column(name = "delivery_island_yn", length = 1)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String deliveryIslandYn = "Y";

    @Column(name = "delivery_island_fee", precision = 19, scale = 4)
    @ColumnDefault("0.0000")
    @Builder.Default
    private BigDecimal deliveryIslandFee = BigDecimal.ZERO;

    @Column(name = "is_default", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @Builder.Default
    private String isDefault = "N";

    /**
     * 출고지 정보를 업데이트합니다.
     */
    public void update(String shipmentType, String shipmentName, String zipcode, String address, String addressDetail,
                       String supplierName, String phone, String mobile, BigDecimal shippingFee, BigDecimal returnFee,
                       BigDecimal exchangeFee, String deliveryIslandYn, BigDecimal deliveryIslandFee, String isDefault) {
        this.shipmentType = shipmentType;
        this.shipmentName = shipmentName;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.supplierName = supplierName;
        this.phone = phone;
        this.mobile = mobile;
        this.shippingFee = shippingFee;
        this.returnFee = returnFee;
        this.exchangeFee = exchangeFee;
        this.deliveryIslandYn = deliveryIslandYn;
        this.deliveryIslandFee = deliveryIslandFee;
        this.isDefault = isDefault;
    }

    /**
     * 출고지 코드를 업데이트합니다.
     */
    public void updateShipmentCode(String shipmentCode) {
        this.shipmentCode = shipmentCode;
    }

    /**
     * 기본 배송지 설정을 해제합니다.
     */
    public void clearDefault() {
        this.isDefault = "N";
    }
}

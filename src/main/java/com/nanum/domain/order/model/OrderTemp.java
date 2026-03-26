package com.nanum.domain.order.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_temp", uniqueConstraints = {
        @UniqueConstraint(name = "uq_order_temp_no", columnNames = "order_no")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temp_id")
    private Long tempId;

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @Column(name = "member_code", nullable = false, length = 50)
    private String memberCode;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "receiver_address")
    private String receiverAddress;

    @Column(name = "receiver_detail")
    private String receiverDetail;

    @Column(name = "receiver_zipcode")
    private String receiverZipcode;

    @Column(name = "delivery_memo")
    private String deliveryMemo;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(name = "used_point")
    @ColumnDefault("0")
    @Builder.Default
    private int usedPoint = 0;

    @Column(name = "member_coupon_id")
    private Long memberCouponId;

    @Column(name = "total_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal totalPrice;

    @Column(name = "delivery_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal deliveryPrice;

    @Column(name = "payment_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal paymentPrice;

    @Column(name = "tax_bill_type", length = 20)
    @Builder.Default
    private String taxBillType = "NONE";

    @Column(name = "items_json", columnDefinition = "TEXT")
    private String itemsJson;

    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private String status = "PAYMENT_WAIT";

    @Version
    @Column(name = "version")
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}

package com.nanum.domain.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.nanum.global.common.dto.BaseEntity;
import com.nanum.domain.member.model.Member;

import com.nanum.domain.order.model.OrderMaster;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payment_master")
public class PaymentMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private OrderMaster orderMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code")
    private Member member;

    // Payment Details
    @Column(name = "total_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal totalPrice;

    @Column(name = "discount_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal discountPrice;

    @Column(name = "used_point", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal usedPoint;

    @Column(name = "used_coupon", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal usedCoupon;

    @Column(name = "delivery_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal deliveryPrice;

    @Column(name = "payment_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal paymentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    // Bank Transfer Info
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_num")
    private String bankAccountNum;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "depositor_name")
    private String depositorName;

    @Column(name = "deposit_deadline")
    private LocalDateTime depositDeadline;

    // Cancel / Refund Info
    @Column(name = "cancel_total_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal cancelTotalPrice;

    @Column(name = "cancel_coupon_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal cancelCouponPrice;

    @Column(name = "cancel_point_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal cancelPointPrice;

    @Column(name = "cancel_delivery_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal cancelDeliveryPrice;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Builder
    public PaymentMaster(OrderMaster orderMaster, Member member, BigDecimal totalPrice, BigDecimal discountPrice,
            BigDecimal usedPoint, BigDecimal usedCoupon, BigDecimal deliveryPrice, BigDecimal paymentPrice,
            PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        this.orderMaster = orderMaster;
        this.member = member;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.usedPoint = usedPoint;
        this.usedCoupon = usedCoupon;
        this.deliveryPrice = deliveryPrice;
        this.paymentPrice = paymentPrice;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
    }
}

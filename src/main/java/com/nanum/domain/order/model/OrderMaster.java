package com.nanum.domain.order.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.nanum.domain.member.model.Member;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "order_master", uniqueConstraints = {
        @UniqueConstraint(name = "uq_order_no", columnNames = "order_no")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "order_name")
    private String orderName;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @Builder.Default
    private OrderStatus status = OrderStatus.PAYMENT_WAIT;

    // Amount Info
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

    @Column(name = "tracking_number")
    private String trackingNumber;

    // Business Methods
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

}

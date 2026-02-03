package com.nanum.user.order.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "order_master", uniqueConstraints = {
        @UniqueConstraint(name = "uq_order_no", columnNames = "order_no")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderMaster extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_name")
    private String orderName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code")
    private com.nanum.user.member.model.Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @Builder.Default
    private OrderStatus status = OrderStatus.PAYMENT_WAIT;

    @Column(name = "total_amount")
    @ColumnDefault("0")
    private Long totalAmount;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_address_detail")
    private String shippingAddressDetail;

    @Column(name = "shipping_zipcode")
    private String shippingZipcode;

    @Column(name = "delivery_msg")
    private String deliveryMsg;

    @Column(name = "tracking_number")
    private String trackingNumber;

    // Business Methods
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

}

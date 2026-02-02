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
    private Long id;

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PAY_WAIT;

    @Column(name = "total_amount", nullable = false)
    @ColumnDefault("0")
    private int totalAmount;

    @Column(name = "discount_amount", nullable = false)
    @ColumnDefault("0")
    private int discountAmount;

    @Column(name = "delivery_fee", nullable = false)
    @ColumnDefault("0")
    private int deliveryFee;

    @Column(name = "payment_amount", nullable = false)
    @ColumnDefault("0")
    private int paymentAmount;

    // Receiver Info
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    @Column(name = "receiver_zipcode", nullable = false)
    private String receiverZipcode;

    @Column(name = "receiver_address", nullable = false)
    private String receiverAddress;

    @Column(name = "receiver_detail", nullable = false)
    private String receiverDetail;

    @Column(name = "delivery_memo")
    private String deliveryMemo;

}

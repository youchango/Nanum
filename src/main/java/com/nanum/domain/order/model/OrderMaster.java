package com.nanum.domain.order.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import com.nanum.domain.member.model.Member;
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
    @JoinColumn(name = "member_code", referencedColumnName = "member_code")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @Builder.Default
    private OrderStatus status = OrderStatus.PAYMENT_WAIT;

    @Column(name = "total_amount")
    @ColumnDefault("0")
    private Long totalAmount;

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

    @Column(name = "delivery_msg")
    private String deliveryMsg;

    @Column(name = "tracking_number")
    private String trackingNumber;

    // Business Methods
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

}

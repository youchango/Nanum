package com.nanum.domain.order.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @Setter
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_seq", nullable = false)
    private Integer orderSeq;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "option_name", length = 100)
    private String optionName;

    @Column(name = "product_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal productPrice;

    @Column(name = "option_price", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal optionPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus;

    @Column(name = "delivery_num", length = 100)
    private String deliveryNum;

    @Column(name = "delivery_corp", length = 200)
    private String deliveryCorp;

    @Column(name = "delivery_date_start")
    private LocalDateTime deliveryDateStart;

    @Column(name = "delivery_date_end")
    private LocalDateTime deliveryDateEnd;

    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "claim_cd", length = 10)
    private String claimCd;

    @Column(name = "claim_type", length = 10)
    private String claimType;

    @Builder.Default
    @Column(name = "cancel_yn", nullable = false, length = 1)
    private String cancelYn = "N";

    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;

    @Column(name = "cancel_price", precision = 19, scale = 4)
    private BigDecimal cancelPrice;

    @Builder.Default
    @Column(name = "refund_yn", nullable = false, length = 1)
    private String refundYn = "N";

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "refund_price", precision = 19, scale = 4)
    private BigDecimal refundPrice;

    @Column(name = "pickup_date_start")
    private LocalDateTime pickupDateStart;

    @Column(name = "pickup_date_end")
    private LocalDateTime pickupDateEnd;

    @Setter
    @Builder.Default
    @Column(name = "review_yn", nullable = false, length = 1)
    private String reviewYn = "N";

}

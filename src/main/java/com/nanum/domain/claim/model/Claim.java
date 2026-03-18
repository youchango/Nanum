package com.nanum.domain.claim.model;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderMaster orderMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code", nullable = false)
    private Member member;

    @Column(name = "site_cd", length = 100)
    private String siteCd;

    /** EXCHANGE / RETURN / REFUND */
    @Column(name = "claim_type", nullable = false, length = 20)
    private String claimType;

    /** REQUESTED / REVIEWING / APPROVED / REJECTED / COMPLETED */
    @Builder.Default
    @Column(name = "claim_status", nullable = false, length = 20)
    private String claimStatus = "REQUESTED";

    /** ORDER_ERROR / CHANGE_OF_MIND / DEFECTIVE / DAMAGED / MISDELIVERY / OTHER */
    @Column(name = "claim_reason", nullable = false, length = 30)
    private String claimReason;

    @Column(name = "claim_reason_detail", columnDefinition = "TEXT")
    private String claimReasonDetail;

    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    /** POINT / CASH */
    @Column(name = "refund_type", length = 20)
    private String refundType;

    @Column(name = "refund_price", precision = 19, scale = 4)
    private BigDecimal refundPrice;

    @Column(name = "refund_bank_name", length = 40)
    private String refundBankName;

    @Column(name = "refund_account_num", length = 40)
    private String refundAccountNum;

    @Column(name = "refund_account_name", length = 40)
    private String refundAccountName;

    @Column(name = "admin_memo", columnDefinition = "TEXT")
    private String adminMemo;

    @Builder.Default
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

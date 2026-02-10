package com.nanum.admin.claim.entity;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@Table(name = "claim")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "site_cd", length = 100)
    private String siteCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", nullable = false)
    private Member member;

    @Column(name = "claim_cd", nullable = false, length = 10)
    private String claimCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderMaster order;

    @Column(name = "order_seq")
    private Integer orderSeq;

    @Column(name = "claim_type", nullable = false, length = 10)
    private String claimType;

    @Column(name = "claim_gubun", length = 10)
    private String claimGubun;

    @Column(name = "claim_reason", columnDefinition = "TEXT")
    private String claimReason;

    @Column(name = "claim_status", nullable = false, length = 20)
    private String claimStatus;

    @Column(name = "claim_date_entry", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime claimDateEntry;

    @Column(name = "claim_check", length = 10)
    private String claimCheck;

    @Column(name = "claim_date_check")
    private LocalDateTime claimDateCheck;

    @Column(name = "claim_check_cd", length = 30)
    private String claimCheckCd;

    @Column(name = "claim_memo", columnDefinition = "TEXT")
    private String claimMemo;

    @Column(name = "vbank_input_name", length = 40)
    private String vbankInputName;

    @Column(name = "vbank_bank_name", length = 40)
    private String vbankBankName;

    @Column(name = "vbank_num", length = 40)
    private String vbankNum;

    @Column(name = "refund_type", length = 20)
    private String refundType;

    @Column(name = "return_zipcode", length = 10)
    private String returnZipcode;

    @Column(name = "return_address", length = 200)
    private String returnAddress;

    @Column(name = "return_detail", length = 200)
    private String returnDetail;

    @Column(name = "refund_check", length = 10)
    private String refundCheck;

    @Column(name = "refund_price", precision = 19, scale = 4)
    private BigDecimal refundPrice;

    @Column(name = "claim_cnt")
    private Integer claimCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "exchange_zipcode", length = 10)
    private String exchangeZipcode;

    @Column(name = "exchange_address", length = 200)
    private String exchangeAddress;

    @Column(name = "exchange_detail", length = 200)
    private String exchangeDetail;

    @Column(name = "refund_status", length = 20)
    private String refundStatus;

    @Column(name = "refund_manager_cd", length = 30)
    private String refundManagerCd;

    @Column(name = "created_by", length = 30)
    private String createdBy;
}

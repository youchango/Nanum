package com.nanum.domain.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.nanum.global.common.dto.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 현금영수증 Entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cash_receipt")
public class CashReceipt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentMaster paymentMaster;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_type")
    private ReceiptType receiptType;

    @Column(name = "identity_num")
    private String identityNum;

    @Column(name = "issue_amount", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal issueAmount;

    @Column(name = "supply_value", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal supplyValue;

    @Column(name = "vat", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal vat;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_status")
    private ReceiptStatus receiptStatus;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Builder
    public CashReceipt(PaymentMaster paymentMaster, ReceiptType receiptType, String identityNum, 
            BigDecimal issueAmount, BigDecimal supplyValue, BigDecimal vat, ReceiptStatus receiptStatus) {
        this.paymentMaster = paymentMaster;
        this.receiptType = receiptType;
        this.identityNum = identityNum;
        this.issueAmount = issueAmount;
        this.supplyValue = supplyValue;
        this.vat = vat;
        this.receiptStatus = receiptStatus;
    }
}

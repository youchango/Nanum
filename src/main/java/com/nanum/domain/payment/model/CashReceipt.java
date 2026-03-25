package com.nanum.domain.payment.model;
 
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cash_receipt")
public class CashReceipt {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_type", length = 20)
    private ReceiptType receiptType;
 
    @Column(name = "identity_num", length = 50)
    private String identityNum;
 
    @Column(name = "issue_amount", precision = 19, scale = 4)
    private BigDecimal issueAmount;
 
    @Column(name = "supply_value", precision = 19, scale = 4)
    private BigDecimal supplyValue;
 
    @Column(name = "vat", precision = 19, scale = 4)
    private BigDecimal vat;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_status", length = 20)
    private ReceiptStatus receiptStatus;
 
    @Column(name = "receipt_url")
    private String receiptUrl;
 
    @Column(name = "issue_date")
    private LocalDateTime issueDate;
 
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @Builder
    public CashReceipt(Payment payment, ReceiptType receiptType, String identityNum, BigDecimal issueAmount,
            BigDecimal supplyValue, BigDecimal vat, ReceiptStatus receiptStatus, String receiptUrl, LocalDateTime issueDate) {
        this.payment = payment;
        this.receiptType = receiptType;
        this.identityNum = identityNum;
        this.issueAmount = issueAmount;
        this.supplyValue = supplyValue;
        this.vat = vat;
        this.receiptStatus = receiptStatus;
        this.receiptUrl = receiptUrl;
        this.issueDate = issueDate;
    }
}

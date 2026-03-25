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
@Table(name = "tax_invoice")
public class TaxInvoice {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
 
    @Column(name = "company_name", length = 100)
    private String companyName;
 
    @Column(name = "ceo_name", length = 50)
    private String ceoName;
 
    @Column(name = "business_reg_num", length = 20)
    private String businessRegNum;
 
    @Column(name = "business_type", length = 50)
    private String businessType;
 
    @Column(name = "business_class", length = 50)
    private String businessClass;
 
    @Column(name = "company_address")
    private String companyAddress;
 
    @Column(name = "email", length = 100)
    private String email;
 
    @Column(name = "issue_amount", precision = 19, scale = 4)
    private BigDecimal issueAmount;
 
    @Column(name = "supply_value", precision = 19, scale = 4)
    private BigDecimal supplyValue;
 
    @Column(name = "vat", precision = 19, scale = 4)
    private BigDecimal vat;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", length = 20)
    private InvoiceStatus invoiceStatus;
 
    @Column(name = "invoice_url")
    private String invoiceUrl;
 
    @Column(name = "issue_date")
    private LocalDateTime issueDate;
 
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @Builder
    public TaxInvoice(Payment payment, String companyName, String ceoName, String businessRegNum,
            String businessType, String businessClass, String companyAddress, String email,
            BigDecimal issueAmount, BigDecimal supplyValue, BigDecimal vat, InvoiceStatus invoiceStatus,
            String invoiceUrl, LocalDateTime issueDate) {
        this.payment = payment;
        this.companyName = companyName;
        this.ceoName = ceoName;
        this.businessRegNum = businessRegNum;
        this.businessType = businessType;
        this.businessClass = businessClass;
        this.companyAddress = companyAddress;
        this.email = email;
        this.issueAmount = issueAmount;
        this.supplyValue = supplyValue;
        this.vat = vat;
        this.invoiceStatus = invoiceStatus;
        this.invoiceUrl = invoiceUrl;
        this.issueDate = issueDate;
    }
}

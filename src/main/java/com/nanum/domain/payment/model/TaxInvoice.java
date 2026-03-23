package com.nanum.domain.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.nanum.global.common.dto.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 세금계산서 Entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tax_invoice")
public class TaxInvoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentMaster paymentMaster;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "ceo_name")
    private String ceoName;

    @Column(name = "business_reg_num")
    private String businessRegNum;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_class")
    private String businessClass;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "email")
    private String email;

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
    @Column(name = "invoice_status")
    private InvoiceStatus invoiceStatus;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Builder
    public TaxInvoice(PaymentMaster paymentMaster, String companyName, String ceoName, 
            String businessRegNum, String businessType, String businessClass, String companyAddress, String email,
            BigDecimal issueAmount, BigDecimal supplyValue, BigDecimal vat, InvoiceStatus invoiceStatus) {
        this.paymentMaster = paymentMaster;
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
    }
}

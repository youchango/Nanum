package com.nanum.admin.payment.dto;

import com.nanum.domain.payment.model.InvoiceStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTaxInvoiceDetailDTO {
    private Long invoiceId;
    private Long paymentId;
    private String orderName;
    private String orderNo;
    private String siteCd;
    private String ordererName;
    
    private String companyName;
    private String ceoName;
    private String businessRegNum;
    private String businessType;
    private String businessClass;
    private String companyAddress;
    private String email;

    private BigDecimal issueAmount;
    private BigDecimal supplyValue;
    private BigDecimal vat;

    private InvoiceStatus invoiceStatus;
    private String invoiceUrl;
    private LocalDateTime issueDate;
}

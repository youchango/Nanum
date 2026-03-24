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
public class AdminTaxInvoiceDTO {
    private Long invoiceId;
    private Long paymentId;
    private String orderName;
    private String orderNo;
    private String siteCd;
    private String ordererName;
    private String companyName;
    private String businessRegNum;
    private BigDecimal issueAmount;
    private InvoiceStatus invoiceStatus;
    private LocalDateTime issueDate;
}

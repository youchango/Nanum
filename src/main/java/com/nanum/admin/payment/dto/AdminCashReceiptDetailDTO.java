package com.nanum.admin.payment.dto;

import com.nanum.domain.payment.model.ReceiptStatus;
import com.nanum.domain.payment.model.ReceiptType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCashReceiptDetailDTO {
    private Long receiptId;
    private Long paymentId;
    private String orderName;
    private String orderNo;
    private String siteCd;
    private String ordererName;
    
    private ReceiptType receiptType;
    private String receiptTypeDesc;
    private String identityNum;
    
    private BigDecimal issueAmount;
    private BigDecimal supplyValue;
    private BigDecimal vat;
    
    private ReceiptStatus receiptStatus;
    private String receiptStatusDesc;
    private String receiptUrl;
    private LocalDateTime issueDate;
}

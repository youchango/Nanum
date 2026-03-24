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
public class AdminCashReceiptDTO {
    private Long receiptId;
    private Long paymentId;
    private String orderName;
    private String orderNo;
    private String siteCd;
    private String ordererName;
    private ReceiptType receiptType;
    private String identityNum;
    private BigDecimal issueAmount;
    private ReceiptStatus receiptStatus;
    private LocalDateTime issueDate;
}

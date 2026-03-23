package com.nanum.admin.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPaymentDTO {
    private Long paymentId;
    private String orderNo; 
    private String orderName;
    private String ordererName;
    private BigDecimal paymentPrice;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String siteCd;
}

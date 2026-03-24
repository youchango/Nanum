package com.nanum.admin.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPaymentDetailDTO {
    private Long paymentId;
    private String orderNo;
    private String orderName;
    private String ordererName;

    // Amounts
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private BigDecimal usedPoint;
    private BigDecimal usedCoupon;
    private BigDecimal deliveryPrice;
    private BigDecimal paymentPrice;

    // Status & Method
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    
    // Deposit / Bank info
    private String bankName;
    private String bankAccountNum;
    private String depositorName;
    
    // Refunds
    private BigDecimal cancelTotalPrice;
    private BigDecimal cancelPointPrice;
    private BigDecimal cancelCouponPrice;
    
    // Site
    private String siteCd;
}

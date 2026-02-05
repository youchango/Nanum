package com.nanum.domain.payment.dto;

import lombok.Data;

@Data
public class PaymentProcessRequest {
    private String paymentMethod;
    private Integer usedPoint;
}

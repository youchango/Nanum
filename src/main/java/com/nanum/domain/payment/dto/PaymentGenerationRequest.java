package com.nanum.domain.payment.dto;

import lombok.Data;

@Data
public class PaymentGenerationRequest {
    private Long memberId;
    private Integer amount;
}

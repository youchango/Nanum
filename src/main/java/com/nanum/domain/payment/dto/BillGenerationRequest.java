package com.nanum.domain.payment.dto;

import lombok.Data;

@Data
public class BillGenerationRequest {
    private Long memberId;
    private Integer amount;
}

package com.nanum.user.payment.dto;

import lombok.Data;

@Data
public class BillGenerationRequest {
    private Long memberId;
    private Integer amount;
}

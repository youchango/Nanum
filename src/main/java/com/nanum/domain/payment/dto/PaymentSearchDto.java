package com.nanum.domain.payment.dto;

import lombok.Data;
import java.time.LocalDate;

import com.nanum.domain.payment.model.PaymentStatus;

@Data
public class PaymentSearchDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String memberName;
}

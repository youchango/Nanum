package com.nanum.user.payment.dto;

import lombok.Data;
import java.time.LocalDate;

import com.nanum.user.payment.model.PaymentStatus;

@Data
public class PaymentSearchDto {
    private String memberCode;
    private String memberName;
    private LocalDate startDate;
    private LocalDate endDate;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
}

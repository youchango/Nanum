package com.nanum.domain.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.nanum.domain.payment.model.Payment;
import com.nanum.domain.payment.model.PaymentStatus;

@Data
public class PaymentDto {
    private Long paymentId;
    private String memberCode;
    private String memberName;
    private Integer paymentAmount;
    private Integer usedPoint;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

    public PaymentDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.memberCode = payment.getMember().getMemberCode();
        this.memberName = payment.getMember().getMemberName();
        this.paymentAmount = payment.getPaymentAmount();
        this.usedPoint = payment.getUsedPoint();
        this.paymentStatus = payment.getPaymentStatus();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentDate = payment.getPaymentDate();
        this.createdAt = payment.getCreatedAt();
    }
}

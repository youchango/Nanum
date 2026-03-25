package com.nanum.domain.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.nanum.domain.payment.model.Payment;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.domain.payment.model.PaymentMethod;

import java.math.BigDecimal;

@Data
public class PaymentDto {
    private Long paymentId;
    private Long orderId;
    private String memberCode;
    private String memberName;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private BigDecimal usedPoint;
    private BigDecimal usedCoupon;
    private BigDecimal deliveryPrice;
    private BigDecimal paymentPrice;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

    public PaymentDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrderMaster() != null ? payment.getOrderMaster().getOrderId() : null;
        this.memberCode = payment.getMember().getMemberCode();
        this.memberName = payment.getMember().getMemberName();
        this.totalPrice = payment.getTotalPrice();
        this.discountPrice = payment.getDiscountPrice();
        this.usedPoint = payment.getUsedPoint();
        this.usedCoupon = payment.getUsedCoupon();
        this.deliveryPrice = payment.getDeliveryPrice();
        this.paymentPrice = payment.getPaymentPrice();
        this.paymentStatus = payment.getPaymentStatus();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentDate = payment.getPaymentDate();
        this.createdAt = payment.getCreatedAt();
    }
}

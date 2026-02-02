package com.nanum.user.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.user.payment.dto.PaymentDto;
import com.nanum.user.payment.dto.PaymentSearchDto;
import com.nanum.user.point.dto.PointDto;
import com.nanum.user.point.dto.PointSearchDto;

public interface BillingService {
    void generateBill(Long memberId, Integer amount);
    void processPayment(Long paymentId, String method, Integer usedPoint);
    void cancelPayment(Long paymentId);
    Page<PaymentDto> searchPayments(PaymentSearchDto searchDto, Pageable pageable);
    PaymentDto getPaymentDetail(Long paymentId);
    Page<PointDto> searchPoints(PointSearchDto searchDto, Pageable pageable);
}

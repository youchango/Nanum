package com.nanum.user.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.domain.payment.dto.PaymentDto;
import com.nanum.domain.payment.dto.PaymentSearchDto;
import com.nanum.domain.point.dto.PointDto;
import com.nanum.domain.point.dto.PointSearchDto;

public interface PaymentService {
    void createPayment(String memberCode, Integer amount);

    void processPayment(Long paymentId, String method, Integer usedPoint);

    void cancelPayment(Long paymentId);

    Page<PaymentDto> searchPayments(PaymentSearchDto searchDto, Pageable pageable);

    PaymentDto getPaymentDetail(Long paymentId);

    Page<PointDto> searchPoints(PointSearchDto searchDto, Pageable pageable);
}

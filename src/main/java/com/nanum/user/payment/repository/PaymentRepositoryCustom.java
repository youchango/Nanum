package com.nanum.user.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.domain.payment.dto.PaymentSearchDto;
import com.nanum.domain.payment.model.Payment;

public interface PaymentRepositoryCustom {
    Page<Payment> searchPayments(PaymentSearchDto searchDto, Pageable pageable);
}

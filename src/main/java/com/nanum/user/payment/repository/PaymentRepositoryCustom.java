package com.nanum.user.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.domain.payment.dto.PaymentSearchDto;
import com.nanum.domain.payment.model.PaymentMaster;

import java.util.List;

public interface PaymentRepositoryCustom {
    Page<PaymentMaster> searchPayments(PaymentSearchDto paymentSearchDto, Pageable pageable);
}

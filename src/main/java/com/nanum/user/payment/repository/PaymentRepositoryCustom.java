package com.nanum.user.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.domain.payment.dto.PaymentSearchDto;
import com.nanum.domain.payment.model.Payment;

import java.util.List;

import com.nanum.admin.payment.dto.AdminPaymentDTO;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.global.common.dto.SearchDTO;

public interface PaymentRepositoryCustom {
    Page<Payment> searchPayments(PaymentSearchDto paymentSearchDto, Pageable pageable);
    
    Page<AdminPaymentDTO> findAdminPayments(SearchDTO searchDTO, PaymentStatus status, String siteCd, Pageable pageable);
}

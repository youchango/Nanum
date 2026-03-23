package com.nanum.admin.payment.service;

import com.nanum.admin.payment.dto.AdminPaymentDTO;
import com.nanum.admin.payment.dto.AdminPaymentDetailDTO;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.global.common.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPaymentService {
    Page<AdminPaymentDTO> getPaymentList(SearchDTO searchDTO, PaymentStatus status, String siteCd, Pageable pageable);
    AdminPaymentDetailDTO getPaymentDetail(Long paymentId);
}

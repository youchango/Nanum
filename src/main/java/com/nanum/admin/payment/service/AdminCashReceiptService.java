package com.nanum.admin.payment.service;

import com.nanum.admin.payment.dto.AdminCashReceiptDTO;
import com.nanum.admin.payment.dto.AdminCashReceiptDetailDTO;
import com.nanum.domain.payment.model.ReceiptStatus;
import com.nanum.global.common.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCashReceiptService {
    Page<AdminCashReceiptDTO> getCashReceiptList(SearchDTO searchDTO, ReceiptStatus status, String siteCd, Pageable pageable);
    AdminCashReceiptDetailDTO getCashReceiptDetail(Long receiptId);
}

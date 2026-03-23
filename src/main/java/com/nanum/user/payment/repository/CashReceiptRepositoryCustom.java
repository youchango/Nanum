package com.nanum.user.payment.repository;

import com.nanum.admin.payment.dto.AdminCashReceiptDTO;
import com.nanum.domain.payment.model.ReceiptStatus;
import com.nanum.global.common.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CashReceiptRepositoryCustom {
    Page<AdminCashReceiptDTO> findAdminCashReceipts(SearchDTO searchDTO, ReceiptStatus status, String siteCd, Pageable pageable);
}

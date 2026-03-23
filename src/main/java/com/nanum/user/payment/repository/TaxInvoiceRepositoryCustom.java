package com.nanum.user.payment.repository;

import com.nanum.admin.payment.dto.AdminTaxInvoiceDTO;
import com.nanum.domain.payment.model.InvoiceStatus;
import com.nanum.global.common.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaxInvoiceRepositoryCustom {
    Page<AdminTaxInvoiceDTO> findAdminTaxInvoices(SearchDTO searchDTO, InvoiceStatus status, String siteCd, Pageable pageable);
}

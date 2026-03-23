package com.nanum.admin.payment.service;
import com.nanum.admin.payment.dto.AdminTaxInvoiceDTO;
import com.nanum.admin.payment.dto.AdminTaxInvoiceDetailDTO;
import com.nanum.domain.payment.model.InvoiceStatus;
import com.nanum.global.common.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminTaxInvoiceService {
    Page<AdminTaxInvoiceDTO> getTaxInvoiceList(SearchDTO searchDTO, InvoiceStatus status, String siteCd, Pageable pageable);
    AdminTaxInvoiceDetailDTO getTaxInvoiceDetail(Long invoiceId);
}

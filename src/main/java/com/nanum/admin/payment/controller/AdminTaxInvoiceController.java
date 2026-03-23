package com.nanum.admin.payment.controller;

import com.nanum.admin.payment.dto.AdminTaxInvoiceDTO;
import com.nanum.admin.payment.dto.AdminTaxInvoiceDetailDTO;
import com.nanum.admin.payment.service.AdminTaxInvoiceService;
import com.nanum.domain.payment.model.InvoiceStatus;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments/tax-invoices")
public class AdminTaxInvoiceController {

    private final AdminTaxInvoiceService adminTaxInvoiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminTaxInvoiceDTO>>> getTaxInvoiceList(
            @ModelAttribute SearchDTO searchDTO,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) String siteCd,
            Pageable pageable) {
        Page<AdminTaxInvoiceDTO> list = adminTaxInvoiceService.getTaxInvoiceList(searchDTO, status, siteCd, pageable);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<AdminTaxInvoiceDetailDTO>> getTaxInvoiceDetail(@PathVariable Long invoiceId) {
        AdminTaxInvoiceDetailDTO detail = adminTaxInvoiceService.getTaxInvoiceDetail(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }
}

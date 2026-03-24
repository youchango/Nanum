package com.nanum.admin.payment.controller;

import com.nanum.admin.payment.dto.AdminCashReceiptDTO;
import com.nanum.admin.payment.dto.AdminCashReceiptDetailDTO;
import com.nanum.admin.payment.service.AdminCashReceiptService;
import com.nanum.domain.payment.model.ReceiptStatus;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments/cash-receipts")
public class AdminCashReceiptController {

    private final AdminCashReceiptService adminCashReceiptService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminCashReceiptDTO>>> getCashReceiptList(
            @ModelAttribute SearchDTO searchDTO,
            @RequestParam(required = false) ReceiptStatus status,
            @RequestParam(required = false) String siteCd,
            Pageable pageable) {
        Page<AdminCashReceiptDTO> list = adminCashReceiptService.getCashReceiptList(searchDTO, status, siteCd, pageable);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{receiptId}")
    public ResponseEntity<ApiResponse<AdminCashReceiptDetailDTO>> getCashReceiptDetail(@PathVariable Long receiptId) {
        AdminCashReceiptDetailDTO detail = adminCashReceiptService.getCashReceiptDetail(receiptId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }
}

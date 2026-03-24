package com.nanum.admin.payment.controller;

import com.nanum.admin.payment.dto.AdminPaymentDTO;
import com.nanum.admin.payment.dto.AdminPaymentDetailDTO;
import com.nanum.admin.payment.service.AdminPaymentService;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payments")
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminPaymentDTO>>> getPaymentList(
            @ModelAttribute SearchDTO searchDTO,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String siteCd,
            Pageable pageable) {
        
        Page<AdminPaymentDTO> list = adminPaymentService.getPaymentList(searchDTO, status, siteCd, pageable);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<AdminPaymentDetailDTO>> getPaymentDetail(@PathVariable Long paymentId) {
        AdminPaymentDetailDTO detail = adminPaymentService.getPaymentDetail(paymentId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }
}

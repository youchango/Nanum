package com.nanum.user.payment.controller;

import com.nanum.domain.payment.dto.TaxBillDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.payment.service.TaxBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "TaxBill", description = "세금계산서/현금영수증 API")
@RestController
@RequestMapping("/api/v1/tax-bill")
@RequiredArgsConstructor
public class TaxBillController {

    private final TaxBillService taxBillService;

    // ==================== 세금계산서 정보 CRUD ====================

    @Operation(summary = "세금계산서 정보 목록", description = "저장된 세금계산서 사업자 정보 목록을 조회합니다.")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<List<TaxBillDTO.TaxInfoResponse>>> getTaxInfoList(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("조회 성공", taxBillService.getTaxInfoList(principal.getName())));
    }

    @Operation(summary = "세금계산서 정보 등록", description = "새 세금계산서 사업자 정보를 등록합니다.")
    @PostMapping("/info")
    public ResponseEntity<ApiResponse<Long>> createTaxInfo(
            @Valid @RequestBody TaxBillDTO.TaxInfoRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("등록되었습니다.", taxBillService.createTaxInfo(principal.getName(), request)));
    }

    @Operation(summary = "세금계산서 정보 수정", description = "저장된 세금계산서 사업자 정보를 수정합니다.")
    @PutMapping("/info/{id}")
    public ResponseEntity<ApiResponse<Void>> updateTaxInfo(
            @PathVariable Long id, @Valid @RequestBody TaxBillDTO.TaxInfoRequest request, Principal principal) {
        taxBillService.updateTaxInfo(principal.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("수정되었습니다.", null));
    }

    @Operation(summary = "세금계산서 정보 삭제", description = "저장된 세금계산서 사업자 정보를 삭제합니다.")
    @DeleteMapping("/info/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTaxInfo(
            @PathVariable Long id, Principal principal) {
        taxBillService.deleteTaxInfo(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다.", null));
    }

    // ==================== 발행 신청 ====================

    @Operation(summary = "발행 신청", description = "주문에 대한 세금계산서 또는 현금영수증 발행을 신청합니다.")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Long>> applyTaxBill(
            @Valid @RequestBody TaxBillDTO.ApplyRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("발행 신청이 완료되었습니다.", taxBillService.applyTaxBill(principal.getName(), request)));
    }

    @Operation(summary = "내 신청 목록", description = "본인의 발행 신청 내역을 페이징하여 조회합니다.")
    @GetMapping("/apply")
    public ResponseEntity<ApiResponse<Page<TaxBillDTO.ApplyResponse>>> getMyApplyList(
            Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("조회 성공", taxBillService.getMyApplyList(principal.getName(), pageable)));
    }

    @Operation(summary = "신청 상세", description = "특정 발행 신청 내역의 상세 정보를 조회합니다.")
    @GetMapping("/apply/{id}")
    public ResponseEntity<ApiResponse<TaxBillDTO.ApplyResponse>> getApplyDetail(
            @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("조회 성공", taxBillService.getApplyDetail(principal.getName(), id)));
    }
}

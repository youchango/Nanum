package com.nanum.admin.inout.controller;

import com.nanum.admin.inout.dto.*;
import com.nanum.admin.inout.service.AdminInoutService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 입출고 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/admin/inout")
@RequiredArgsConstructor
@Tag(name = "Admin Inout", description = "관리자 입출고 관리 API")
public class AdminInoutController implements ResponseSupport {

    private final AdminInoutService adminInoutService;

    /**
     * 입고 등록
     */
    @PostMapping("/inbound")
    @Operation(summary = "입고 등록", description = "신규 입고 내역을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> registerInbound(@RequestBody InboundRequest request) {
        String ioCode = adminInoutService.registerInbound(request);
        return success(ioCode);
    }

    /**
     * 출고 등록
     */
    @PostMapping("/outbound")
    @Operation(summary = "출고 등록", description = "신규 출고 내역을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> registerOutbound(@RequestBody OutboundRequest request) {
        String ioCode = adminInoutService.registerOutbound(request);
        return success(ioCode);
    }

    /**
     * 입고 목록 조회
     */
    @GetMapping("/inbound")
    @Operation(summary = "입고 목록 조회", description = "상세 입고 내역 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<InoutDetailResponse>>> searchInboundDetails(InoutSearchDTO searchDTO) {
        searchDTO.setIoType("IN");
        Page<InoutDetailResponse> response = adminInoutService.searchInoutDetails(searchDTO);
        return success(response);
    }

    /**
     * 출고 목록 조회
     */
    @GetMapping("/outbound")
    @Operation(summary = "출고 목록 조회", description = "상세 출고 내역 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<InoutDetailResponse>>> searchOutboundDetails(InoutSearchDTO searchDTO) {
        searchDTO.setIoType("OUT");
        Page<InoutDetailResponse> response = adminInoutService.searchInoutDetails(searchDTO);
        return success(response);
    }

    /**
     * 입출고 현황 조회
     */
    @GetMapping("/status")
    @Operation(summary = "입출고 현황 조회", description = "마스터 기준 입출고 현황 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<InoutStatusResponse>>> searchInoutStatus(InoutSearchDTO searchDTO) {
        Page<InoutStatusResponse> response = adminInoutService.searchInoutStatus(searchDTO);
        return success(response);
    }

    /**
     * 입출고용 상품 검색 (실재고 포함)
     */
    @GetMapping("/products")
    @Operation(summary = "입출고용 상품 검색", description = "입출고 등록 시 필요한 상품 정보를 실재고와 함께 조회합니다.")
    public ResponseEntity<ApiResponse<Page<InoutProductResponse>>> searchProductsForInout(InoutSearchDTO searchDTO) {
        Page<InoutProductResponse> response = adminInoutService.searchProductsForInout(searchDTO);
        return success(response);
    }

    /**
     * 출고 등록용 입고 내역 검색 (잔량 포함)
     */
    @GetMapping("/inbound/available")
    @Operation(summary = "가용 입고 내역 조회", description = "출고 등록 시 원천 입고 데이터로 사용할 가용(잔량 > 0) 입고 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<InoutDetailResponse>>> searchInboundForOutbound(InoutSearchDTO searchDTO) {
        Page<InoutDetailResponse> response = adminInoutService.searchInboundForOutbound(searchDTO);
        return success(response);
    }

    /**
     * 입출고 상세 내역 조회 (Modal용)
     */
    @GetMapping("/status/{ioCode}")
    @Operation(summary = "입출고 상세 조회", description = "특정 입출고 마스터 코드(ioCode)에 해당하는 전체 상세 품목을 조회합니다.")
    public ResponseEntity<ApiResponse<List<InoutDetailResponse>>> getInoutDetails(@PathVariable String ioCode) {
        List<InoutDetailResponse> response = adminInoutService.getInoutDetails(ioCode);
        return success(response);
    }
}

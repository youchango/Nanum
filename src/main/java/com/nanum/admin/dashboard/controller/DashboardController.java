package com.nanum.admin.dashboard.controller;

import com.nanum.admin.dashboard.dto.DashboardDTO;
import com.nanum.admin.dashboard.service.DashboardService;
import com.nanum.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "쇼핑몰 통합 대시보드 API")
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "대시보드 요약 정보 조회", description = "최근 주문, 반품, 결제, 포인트, 문의, 배송 내역을 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboardSummary(
            @RequestParam(required = false) String siteCd) {
        DashboardDTO dashboardDTO = dashboardService.getDashboardSummary(siteCd);
        return ResponseEntity.ok(ApiResponse.success(dashboardDTO));
    }
}

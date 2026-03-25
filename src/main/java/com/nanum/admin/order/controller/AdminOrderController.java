package com.nanum.admin.order.controller;

import com.nanum.domain.order.dto.AdminOrderSearchDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.admin.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "AdminOrder", description = "AdminOrder API")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "주문 목록 조회", description = "고급 검색 필터를 사용하여 주문 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<Map<String, Object>> getAllOrders(AdminOrderSearchDTO searchDTO) {
        return ApiResponse.success(adminOrderService.getOrders(searchDTO));
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보 및 품목 리스트를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<OrderDTO.DetailResponse> getOrderDetail(@PathVariable Long id) {
        return ApiResponse.success(adminOrderService.getOrderDetail(id));
    }

    @Operation(summary = "주문 상태 수정", description = "특정 주문 ID에 대해 결제/배송/취소 등 주문의 진행 상태를 요청받은 값으로 업데이트합니다.")
    @PatchMapping("/{id}/status")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestBody OrderDTO.StatusUpdateRequest request) {
        adminOrderService.updateStatus(id, request);
        return ApiResponse.success("Order Status Updated");
    }
}

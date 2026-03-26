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

    @Operation(summary = "결제 확인 처리", description = "결제대기 상태의 주문을 결제완료 처리하고 배송준비 상태로 변경합니다.")
    @PostMapping("/{id}/confirm-payment")
    public ApiResponse<String> confirmPayment(@PathVariable Long id) {
        adminOrderService.confirmPayment(id);
        return ApiResponse.success("Payment Confirmed");
    }

    @Operation(summary = "주문 취소 처리", description = "결제대기 또는 배송준비 상태의 주문을 취소 처리합니다.")
    @PostMapping("/{id}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable Long id) {
        adminOrderService.cancelOrder(id);
        return ApiResponse.success("Order Cancelled");
    }
}

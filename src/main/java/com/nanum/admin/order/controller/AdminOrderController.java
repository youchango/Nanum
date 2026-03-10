package com.nanum.admin.order.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.admin.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "AdminOrder", description = "AdminOrder API")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "주문 목록 조회", description = "데이터베이스의 order 테이블에서 전체 주문 목록을 조회합니다. 사이트 코드(siteCd)가 있을 경우 해당 사이트의 주문만 필터링합니다.")
    @GetMapping
    public ApiResponse<List<OrderDTO.Response>> getAllOrders(@RequestParam(required = false) String siteCd) {
        return ApiResponse.success(adminOrderService.getOrders(siteCd));
    }

    @Operation(summary = "주문 상태 수정", description = "특정 주문 ID에 대해 결제/배송/취소 등 주문의 진행 상태를 요청받은 값으로 업데이트합니다.")
    @PatchMapping("/{id}/status")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestBody OrderDTO.StatusUpdateRequest request) {
        adminOrderService.updateStatus(id, request);
        return ApiResponse.success("Order Status Updated");
    }
}

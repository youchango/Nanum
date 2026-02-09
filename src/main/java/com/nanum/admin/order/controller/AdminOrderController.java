package com.nanum.admin.order.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.admin.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ApiResponse<List<OrderDTO.Response>> getAllOrders(@RequestParam(required = false) String siteCd) {
        return ApiResponse.success(adminOrderService.getOrders(siteCd));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestBody OrderDTO.StatusUpdateRequest request) {
        adminOrderService.updateStatus(id, request);
        return ApiResponse.success("Order Status Updated");
    }
}

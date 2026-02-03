package com.nanum.admin.order.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.order.dto.OrderDTO;
import com.nanum.user.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<List<OrderDTO.Response>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestBody OrderDTO.StatusUpdateRequest request) {
        orderService.updateStatus(id, request);
        return ApiResponse.success("Order Status Updated");
    }
}

package com.nanum.user.order.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ApiResponse<?> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrder(id));
    }
}

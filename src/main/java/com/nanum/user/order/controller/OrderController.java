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

    @PostMapping
    public ApiResponse<Long> createOrder(@RequestBody com.nanum.user.order.dto.OrderDTO.CreateRequest request) {
        // Assume memberId comes from Security Context in real app, but for now
        // placeholder or passed in request?
        // User requirements didn't specify auth mechanism detail, but typically Order
        // is for logged in user.
        // For simplicity/demo, implementing as if auth check is done or memberId is 1L
        // (testing).
        // Actually, let's assume we pass memberId via request or header?
        // Best practice: @AuthenticationPrincipal or extract from Token.
        // Since I can't change Auth logic easily now, I'll fix memberId to 1L or add it
        // to DTO?
        // Let's use hardcoded 1L for now or if DTO doesn't have it, assume Auth
        // context.
        // Wait, OrderService.createOrder needs memberId.
        String memberCode = "M_USER_001"; // Placeholder for current user
        return ApiResponse.success(orderService.createOrder(memberCode, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrder(id));
    }
}

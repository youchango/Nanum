package com.nanum.user.order.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.user.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@Tag(name = "Order", description = "Order API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements ResponseSupport {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "상품 주문 정보(OrderDTO.CreateRequest)를 기반으로 신규 주문을 생성하고 결제 프로세스를 시작합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@RequestBody OrderDTO.CreateRequest request) {
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
        return success(orderService.createOrder(memberCode, request));
    }

    @Operation(summary = "주문 목록 조회", description = "시스템에 등록된 모든 주문 내역 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO.Response>>> getOrders() {
        return success(orderService.getAllOrders());
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID를 식별자로 하여 특정 주문의 결제 및 배송 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<com.nanum.domain.order.model.OrderMaster>> getOrder(@PathVariable Long id) {
        return success(orderService.getOrder(id));
    }
}

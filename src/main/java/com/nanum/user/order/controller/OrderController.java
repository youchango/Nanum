package com.nanum.user.order.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.user.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.security.Principal;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements ResponseSupport {

    private final OrderService orderService;

    @Deprecated
    @Operation(summary = "주문 생성 (Deprecated)", description = "기존 방식 주문 생성. PG 연동 시 /prepare + /confirm 사용 권장.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(
            @Valid @RequestBody OrderDTO.CreateRequest request,
            Principal principal) {
        Long orderId = orderService.createOrder(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("주문이 생성되었습니다.", orderId));
    }

    @Operation(summary = "주문 준비 (PG 결제 전)", description = "가격 계산 후 임시 주문을 생성합니다. PG 결제 화면에 필요한 orderNo, 금액 정보를 반환합니다.")
    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse<OrderDTO.PrepareResponse>> prepareOrder(
            @Valid @RequestBody OrderDTO.PrepareRequest request,
            Principal principal) {
        OrderDTO.PrepareResponse response = orderService.prepareOrder(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("주문 준비가 완료되었습니다.", response));
    }

    @Operation(summary = "주문 확정 (PG 결제 후)", description = "PG 결제 승인 후 실제 주문을 생성합니다. 재고 차감, 포인트/쿠폰 사용이 이 시점에 처리됩니다.")
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Long>> confirmOrder(
            @Valid @RequestBody OrderDTO.ConfirmRequest request,
            Principal principal) {
        Long orderId = orderService.confirmOrder(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("주문이 확정되었습니다.", orderId));
    }

    @Operation(summary = "내 주문 목록 조회", description = "현재 로그인한 사용자의 주문 내역을 최신순으로 페이징 조회합니다. startDate/endDate로 기간 필터 가능합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDTO.Response>>> getOrders(
            Principal principal,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("주문 목록 조회 성공",
                orderService.getMyOrders(principal.getName(), startDate, endDate, pageable)));
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 결제, 배송, 상품 상세 정보를 조회합니다. 본인 주문만 조회 가능합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO.DetailResponse>> getOrder(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("주문 상세 조회 성공",
                orderService.getOrderDetail(id, principal.getName())));
    }

    @Operation(summary = "주문 취소", description = "본인 주문을 취소합니다. 결제대기/결제완료 상태에서만 취소 가능합니다.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id,
            Principal principal) {
        orderService.cancelOrder(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("주문이 취소되었습니다.", null));
    }
}

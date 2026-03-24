package com.nanum.user.payment.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.order.service.OrderService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@Tag(name = "Payment Webhook", description = "PG 결제 웹훅 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final OrderService orderService;

    @Operation(summary = "PG 결제 웹훅", description = "PG사에서 결제 결과를 통보하는 웹훅 엔드포인트입니다. 항상 200을 반환합니다.")
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> handleWebhook(@RequestBody WebhookRequest request) {
        log.info("[PG Webhook] 수신 - orderNo: {}, paymentKey: {}, status: {}",
                request.getOrderNo(), request.getPaymentKey(), request.getStatus());

        try {
            if ("DONE".equalsIgnoreCase(request.getStatus()) || "APPROVED".equalsIgnoreCase(request.getStatus())) {
                orderService.confirmOrderByOrderNo(request.getOrderNo(), request.getPaymentKey());
            } else if ("DEPOSIT_CONFIRMED".equalsIgnoreCase(request.getStatus())) {
                // 가상계좌/무통장 입금 확인
                orderService.confirmDepositByOrderNo(request.getOrderNo());
            } else {
                log.warn("[PG Webhook] 미처리 상태: {}", request.getStatus());
            }
        } catch (Exception e) {
            // 웹훅은 항상 200 반환 (PG 재시도 방지)
            log.error("[PG Webhook] 처리 중 오류 - orderNo: {}, error: {}", request.getOrderNo(), e.getMessage(), e);
        }

        return ResponseEntity.ok(ApiResponse.success("OK", null));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WebhookRequest {
        private String paymentKey;
        private String orderNo;
        private String status;
    }
}

package com.nanum.user.payment.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mock PG 결제 서비스.
 * 실제 PG 연동 시 이 클래스의 confirm/refund 메서드만 교체하면 됩니다.
 */
@Slf4j
@Service
public class PgPaymentService {

    /**
     * PG 결제 승인 (Mock — 항상 성공)
     */
    public PgConfirmResult confirm(String paymentKey, String orderNo, BigDecimal amount) {
        log.info("[PG Mock] 결제 승인 요청 - paymentKey: {}, orderNo: {}, amount: {}", paymentKey, orderNo, amount);
        return PgConfirmResult.builder()
                .success(true)
                .paymentKey(paymentKey)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    /**
     * PG 결제 환불 (Mock — 항상 성공)
     */
    public boolean refund(String paymentKey, BigDecimal amount) {
        log.info("[PG Mock] 환불 요청 - paymentKey: {}, amount: {}", paymentKey, amount);
        return true;
    }

    @Data
    @Builder
    public static class PgConfirmResult {
        private boolean success;
        private String paymentKey;
        private LocalDateTime approvedAt;
        private String errorMessage;
    }
}

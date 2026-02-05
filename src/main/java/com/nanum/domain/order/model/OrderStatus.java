package com.nanum.domain.order.model;

public enum OrderStatus {
    PAYMENT_WAIT, // 결제 대기
    PAID, // 결제 완료
    PREPARING, // 상품 준비중
    SHIPPING, // 배송중
    DELIVERED, // 배송 완료
    CANCELLED, // 취소됨
    REFUNDED // 환불됨
}

package com.nanum.domain.order.model;

public enum OrderStatus {
    PAYMENT_WAIT, // 결제 대기
    PAYMENT_FAILED, // 결제 실패 (추가)
    PAID, // 결제 완료
    PREPARING, // 상품 준비중
    SHIPPING, // 배송중
    DELIVERED, // 배송 완료
    PURCHASE_CONFIRMED, // 구매 확정 (추가: 정산 기준)
    CANCELLED, // 취소됨
    RETURN_REQUESTED, // 반품 신청 (추가)
    RETURNING, // 반품 회수 중 (추가)
    REFUNDED, // 환불 완료 (돈까지 입금됨)
    EXCHANGE_REQUESTED, // 교환 신청 (추가)
    EXCHANGING, // 교환 회수 중 (추가)
    EXCHANGED // 교환 완료 (추가)
}

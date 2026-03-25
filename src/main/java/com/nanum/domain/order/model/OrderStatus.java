package com.nanum.domain.order.model;

public enum OrderStatus {
    PAYMENT_WAIT, // 결제대기
    PREPARING, // 배송준비
    SHIPPING, // 배송중
    DELIVERED, // 배송완료
    CANCELLED, // 주문취소
    RETURN_REQUEST, // 반품요청
    EXCHANGE_REQUEST // 교환요청
}

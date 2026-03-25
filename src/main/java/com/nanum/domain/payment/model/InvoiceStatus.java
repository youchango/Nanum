package com.nanum.domain.payment.model;
 
import lombok.Getter;
import lombok.RequiredArgsConstructor;
 
/**
 * 세금계산서 발행 상태
 */
@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {
    REQUESTED("요청"),
    ISSUED("발급완료"),
    CANCELLED("취소"),
    FAILED("실패");
 
    private final String description;
}

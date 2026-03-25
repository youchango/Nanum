package com.nanum.domain.payment.model;
 
import lombok.Getter;
import lombok.RequiredArgsConstructor;
 
/**
 * 현금영수증 상태
 */
@Getter
@RequiredArgsConstructor
public enum ReceiptStatus {
    REQUESTED("요청"), 
    ISSUED("발급완료"), 
    CANCELLED("취소"), 
    FAILED("실패");
 
    private final String description;
}

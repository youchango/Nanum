package com.nanum.domain.payment.model;

/**
 * 세금계산서 발행 상태
 */
public enum InvoiceStatus {
    REQUESTED,
    ISSUED,
    CANCELLED,
    FAILED
}

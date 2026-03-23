package com.nanum.domain.payment.model;

/**
 * 현금영수증 상태
 */
public enum ReceiptStatus {
    REQUESTED, 
    ISSUED, 
    CANCELLED, 
    FAILED
}

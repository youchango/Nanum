package com.nanum.domain.payment.model;
 
import lombok.Getter;
import lombok.RequiredArgsConstructor;
 
/**
 * 현금영수증 종류 (소득공제, 지출증빙)
 */
@Getter
@RequiredArgsConstructor
public enum ReceiptType {
    INCOME_DEDUCTION("소득공제"),
    EXPENDITURE_PROOF("지출증빙");
 
    private final String description;
}

package com.nanum.domain.payment.model;

import com.nanum.domain.member.model.Member;
import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 세금계산서/현금영수증 발행 신청
 */
@Entity
@Table(name = "tax_bill_apply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TaxBillApply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_bill_id")
    private Long id;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code", nullable = false)
    private Member member;

    @Column(name = "order_no", nullable = false, length = 50)
    private String orderNo;

    @Column(name = "order_id")
    private Long orderId;

    /** 발행 유형: TAX_BILL(세금계산서), CASH_RECEIPT(현금영수증) */
    @Column(name = "bill_type", nullable = false, length = 20)
    private String billType;

    /** 신청 상태: REQUESTED(신청), ISSUED(발행완료), CANCELLED(취소), FAILED(실패) */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "REQUESTED";

    // === 세금계산서 전용 (사업자 정보) ===

    /** 사업자등록번호 */
    @Column(name = "biz_reg_num", length = 20)
    private String bizRegNum;

    /** 상호명 */
    @Column(name = "biz_name", length = 100)
    private String bizName;

    /** 대표자명 */
    @Column(name = "biz_rep_name", length = 30)
    private String bizRepName;

    /** 업태 */
    @Column(name = "biz_category", length = 50)
    private String bizCategory;

    /** 종목 */
    @Column(name = "biz_detail_category", length = 100)
    private String bizDetailCategory;

    /** 사업장 주소 */
    @Column(name = "biz_address", length = 500)
    private String bizAddress;

    /** 이메일 (발행 수신용) */
    @Column(name = "biz_email", length = 100)
    private String bizEmail;

    /** 연락처 */
    @Column(name = "biz_mobile", length = 20)
    private String bizMobile;

    // === 현금영수증 전용 ===

    /** 현금영수증 발행 용도: INCOME(소득공제), EXPENSE(지출증빙) */
    @Column(name = "receipt_purpose", length = 20)
    private String receiptPurpose;

    /** 현금영수증 식별번호 (휴대폰번호 또는 사업자번호) */
    @Column(name = "receipt_id_num", length = 30)
    private String receiptIdNum;

    // === 발행 결과 ===

    /** 발행일 */
    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    /** 국세청 승인번호 */
    @Column(name = "nts_confirm_num", length = 30)
    private String ntsConfirmNum;

    /** 취소일 */
    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;

    /** 실패 사유 */
    @Column(name = "failed_reason", length = 2000)
    private String failedReason;

    // === 비즈니스 메서드 ===

    public void markIssued(String ntsConfirmNum) {
        this.status = "ISSUED";
        this.issueDate = LocalDateTime.now();
        this.ntsConfirmNum = ntsConfirmNum;
    }

    public void markFailed(String reason) {
        this.status = "FAILED";
        this.failedReason = reason;
    }

    public void markCancelled() {
        this.status = "CANCELLED";
        this.cancelDate = LocalDateTime.now();
    }
}

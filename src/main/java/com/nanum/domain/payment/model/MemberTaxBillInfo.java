package com.nanum.domain.payment.model;

import com.nanum.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 회원별 세금계산서/현금영수증 기본 정보
 * - 한번 입력하면 저장, 다음 신청 시 자동 채움
 */
@Entity
@Table(name = "member_tax_bill_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberTaxBillInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_info_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code", nullable = false)
    private Member member;

    /** 정보 유형: TAX_BILL(세금계산서), CASH_RECEIPT(현금영수증) */
    @Column(name = "info_type", nullable = false, length = 20)
    private String infoType;

    // === 세금계산서 사업자 정보 ===

    @Column(name = "biz_reg_num", length = 20)
    private String bizRegNum;

    @Column(name = "biz_name", length = 100)
    private String bizName;

    @Column(name = "biz_rep_name", length = 30)
    private String bizRepName;

    @Column(name = "biz_category", length = 50)
    private String bizCategory;

    @Column(name = "biz_detail_category", length = 100)
    private String bizDetailCategory;

    @Column(name = "biz_address", length = 500)
    private String bizAddress;

    @Column(name = "biz_email", length = 100)
    private String bizEmail;

    @Column(name = "biz_mobile", length = 20)
    private String bizMobile;

    @Column(name = "dam_name", length = 30)
    private String damName;

    // === 현금영수증 정보 ===

    /** 소득공제 / 지출증빙 */
    @Column(name = "receipt_purpose", length = 20)
    private String receiptPurpose;

    /** 휴대폰번호 또는 사업자번호 */
    @Column(name = "receipt_id_num", length = 30)
    private String receiptIdNum;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String bizRegNum, String bizName, String bizRepName,
                       String bizCategory, String bizDetailCategory, String bizAddress,
                       String bizEmail, String bizMobile, String damName,
                       String receiptPurpose, String receiptIdNum) {
        this.bizRegNum = bizRegNum;
        this.bizName = bizName;
        this.bizRepName = bizRepName;
        this.bizCategory = bizCategory;
        this.bizDetailCategory = bizDetailCategory;
        this.bizAddress = bizAddress;
        this.bizEmail = bizEmail;
        this.bizMobile = bizMobile;
        this.damName = damName;
        this.receiptPurpose = receiptPurpose;
        this.receiptIdNum = receiptIdNum;
        this.updatedAt = LocalDateTime.now();
    }
}

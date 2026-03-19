package com.nanum.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.Date;
import java.sql.Timestamp;

/**
 * DB의 member 테이블과 매핑되는 도메인 객체(Entity)입니다.
 * 회원의 상세 정보를 담고 있습니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 식별 ID (PK)

    @Column(name = "member_code", nullable = false, unique = true)
    private String memberCode; // 회원 코드 (Natural Key) 'M_TYPE_001'

    @Column(name = "member_name")
    private String memberName; // 회원명

    @Column(name = "member_id", unique = true, nullable = false)
    private String memberId; // 로그인 ID

    private String password; // 암호화된 비밀번호

    private String phone; // 일반 전화번호

    @Column(name = "mobile_phone")
    private String mobilePhone; // 휴대전화번호

    private String zipcode; // 우편번호
    private String address; // 주소

    @Column(name = "address_detail")
    private String addressDetail; // 상세주소

    private String email; // 이메일
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private MemberRole role; // 권한 (ROLE_BIZ, ROLE_USER, ROLE_VETERAN)

    @Column(name = "member_type")
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private MemberType memberType; // 회원 유형 (U: 일반, B: 기업, V: 보훈)

    @Column(name = "apply_yn", length = 1, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'N'")
    @Builder.Default
    private String applyYn = "N"; // 승인 여부

    @Column(name = "memo", length = 2000)
    private String memo; // 관리자 메모

    @Column(name = "sms_yn", length = 1, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'N'")
    @Builder.Default
    private String smsYn = "N"; // SMS 수신 동의 (Y/N)

    @Column(name = "email_yn", length = 1, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'N'")
    @Builder.Default
    private String emailYn = "N"; // 이메일 수신 동의 (Y/N)

    @Column(name = "login_fail_count", nullable = false)
    @org.hibernate.annotations.ColumnDefault("0")
    @Builder.Default
    private Integer loginFailCount = 0; // 로그인 실패횟수

    @Column(name = "withdraw_yn", nullable = false)
    @org.hibernate.annotations.ColumnDefault("'N'")
    @Builder.Default
    private String withdrawYn = "N"; // 탈퇴 여부 (Y/N)

    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt; // 탈퇴일

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 가입일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.withdrawYn == null) {
            this.withdrawYn = "N";
        }
    }

    /**
     * JSP fmt:formatDate 태그 사용을 위해 LocalDateTime을 Date로 변환하는 편의 메서드입니다.
     * 
     * @return java.util.Date 객체
     */
    public Date getCreatedAtAsDate() {
        return createdAt == null ? null : Timestamp.valueOf(createdAt);
    }

    // MemberJob removed for E-Commerce migration
}

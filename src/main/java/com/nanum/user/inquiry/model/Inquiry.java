package com.nanum.user.inquiry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private int inquiryId; // 문의 ID (PK)

    @Column(name = "writer_id")
    private String memberId; // 작성자 회원 ID (FK) - DB column name might be writer_id based on XML

    @Column(name = "inquiry_type")
    private int inquiryTypeCode; // 문의 유형 코드 (FK -> Code)

    @Column(name = "title")
    private String title; // 제목

    @Column(name = "content")
    private String content; // 내용

    @Column(name = "answer")
    private String answer; // 답변 내용

    @Column(name = "status")
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private InquiryStatus inquiryStatus; // 상태

    @Transient
    private String writerId; // Duplicate of memberId/writer_id? Mapped as writer_id in XML. check usage.
    // In XML: <result property="memberId" column="writer_id" /> AND <result
    // property="writerId" column="writer_id" />
    // It seems they are the same. I'll map memberId to writer_id column.

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;// 생성일

    @Column(name = "answerer_id")
    private String answererId; // 답변자 ID (FK)

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;// 답변일

    @Column(name = "deleted_by")
    private String deletedBy; // 삭제자 ID

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;// 삭제일

    @Column(name = "delete_yn")
    private String deleteYn; // 삭제 여부

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "writer_id", insertable = false, updatable = false)
    @lombok.ToString.Exclude
    private com.nanum.user.member.model.Member writer;

    // inquiry_type joins with Code table, where code_id matches inquiry_type
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "inquiry_type", insertable = false, updatable = false)
    @lombok.ToString.Exclude
    private com.nanum.user.code.model.Code typeCode;

    // 조인 또는 추가 필드
    @Transient
    private String writerName; // 작성자명

    @Transient
    private String writerLogin; // 작성자 아이디

    @Transient
    private String inquiryTypeName; // 문의 유형명 (Code 테이블 조인)

    @Transient
    private String answererName; // 답변자명

    public String getWriterName() {
        if (writerName != null)
            return writerName;
        return writer != null ? writer.getMemberName() : null;
    }

    public String getWriterLogin() {
        if (writerLogin != null)
            return writerLogin;
        return writer != null ? writer.getMemberId() : null;
    }

    public String getInquiryTypeName() {
        if (inquiryTypeName != null)
            return inquiryTypeName;
        return typeCode != null ? typeCode.getCodeName() : null;
    }

    // MemberType 등 추가 정보가 필요할 수 있음

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}

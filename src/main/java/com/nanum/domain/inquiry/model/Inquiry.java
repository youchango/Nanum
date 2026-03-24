package com.nanum.domain.inquiry.model;

import com.nanum.global.common.dto.BaseEntity;
import com.nanum.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE inquiry SET delete_yn = 'Y', deleted_at = NOW() WHERE inquiry_id = ?")
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type", nullable = false, length = 20)
    private InquiryType type;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "order_no", length = 50)
    private String orderNo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InquiryStatus status = InquiryStatus.WAIT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_code", nullable = false, referencedColumnName = "member_code")
    private Member writer;

    @Column(name = "answerer_code", insertable = false, updatable = false)
    private String answererCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerer_code", referencedColumnName = "manager_code")
    private com.nanum.admin.manager.entity.Manager answerer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "is_secret", length = 1, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'N'")
    @Builder.Default
    private String isSecret = "N";

    public void reply(String answer, com.nanum.admin.manager.entity.Manager answerer) {
        this.answer = answer;
        this.answerer = answerer;
        this.answeredAt = LocalDateTime.now();
        this.status = InquiryStatus.COMPLETE;
    }
}

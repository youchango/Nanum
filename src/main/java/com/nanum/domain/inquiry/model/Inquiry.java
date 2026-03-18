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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerer_code", referencedColumnName = "member_code")
    private Member answerer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    public void reply(String answer, Member answerer) {
        this.answer = answer;
        this.answerer = answerer;
        this.answeredAt = LocalDateTime.now();
        this.status = InquiryStatus.COMPLETE;
    }
}

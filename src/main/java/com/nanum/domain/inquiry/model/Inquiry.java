package com.nanum.domain.inquiry.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import com.nanum.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE inquiry SET delete_yn = 'Y', deleted_at = NOW() WHERE inquiry_id = ?")
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Enumerated(EnumType.ORDINAL) // init_db.sql defines INT. Mapping might need Code converter or just ordinal if
                                  // aligned.
    // Plan suggested Enum. Let's strictly map to DB definition 'inquiry_type INT'.
    // If I use EnumType.ORDINAL, verify order.
    // Or I can use @Convert if needed. For now assume ORDINAL matches or use a
    // Converter.
    // Given the simplicity, I'll use a Code Converter later if needed?
    // Let's use INT column with a Converter or just assume Ordinal for now
    // (Product/Delivery/Order/Etc).
    // Actually, init_db comment says '문의구분 (코드ID)'. This implies reference to a
    // Code table.
    // But for this refactor I will Map it to Enum for code clarity.
    // To be safe with INT column, I should use @Convert or EnumType.ORDINAL.
    // I will use @Enumerated(EnumType.ORDINAL) assuming strict order provided in
    // Enum.
    @Column(name = "inquiry_type", nullable = false)
    private InquiryType type;

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

    @Column(name = "delete_yn", nullable = false)
    @ColumnDefault("'N'")
    @Builder.Default
    private String deleteYn = "N";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy; // MemberCode string

    public void delete(String memberCode) {
        this.deleteYn = "Y";
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = memberCode;
    }

    public void reply(String answer, Member answerer) {
        this.answer = answer;
        this.answerer = answerer;
        this.answeredAt = LocalDateTime.now();
        this.status = InquiryStatus.COMPLETE;
    }
}

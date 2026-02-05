package com.nanum.domain.content.model;

import com.nanum.global.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "content")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE content SET deleted_yn = 'Y', deleted_at = NOW() WHERE content_id = ?")
@Where(clause = "deleted_yn = 'N'")
public class Content extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "content_type", nullable = false)
    private ContentType type;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(name = "content_body", nullable = false, columnDefinition = "LONGTEXT")
    private String contentBody;

    @Column(name = "url_info")
    private String urlInfo;

    @Column(name = "deleted_yn", nullable = false)
    @ColumnDefault("'N'")
    @Builder.Default
    private String deletedYn = "N";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    // Helper for update
    public void update(ContentType type, String subject, String contentBody, String urlInfo) {
        this.type = type;
        this.subject = subject;
        this.contentBody = contentBody;
        this.urlInfo = urlInfo;
    }
}

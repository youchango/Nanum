package com.nanum.domain.content.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "content")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE content SET delete_yn = 'Y', deleted_at = NOW() WHERE content_id = ?")
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "content_type", nullable = false)
    private ContentType type;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(name = "content_body", nullable = false, columnDefinition = "LONGTEXT")
    private String contentBody;

    @Column(name = "url_info")
    private String urlInfo;

    // Helper for update
    public void update(String siteCd, ContentType type, String subject, String contentBody, String urlInfo) {
        this.siteCd = siteCd;
        this.type = type;
        this.subject = subject;
        this.contentBody = contentBody;
        this.urlInfo = urlInfo;
    }
}

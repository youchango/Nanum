package com.nanum.user.content.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private int contentId;

    @Column(name = "content_type")
    private Integer contentTypeCode; // Code ID (FK)

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "content_type", insertable = false, updatable = false)
    @lombok.ToString.Exclude
    private com.nanum.user.code.model.Code typeCode;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content_body")
    private String contentBody;

    @Column(name = "url_info")
    private String urlInfo;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_yn", insertable = false)
    private String deletedYn;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "created_by", insertable = false, updatable = false)
    @lombok.ToString.Exclude
    private com.nanum.user.member.model.Member creator;

    // 조인/추가 필드
    @Transient
    private String createdByName;

    @Transient
    private String contentTypeName;

    public String getCreatedByName() {
        if (createdByName != null)
            return createdByName;
        return creator != null ? creator.getMemberName() : null;
    }

    public String getContentTypeName() {
        if (contentTypeName != null)
            return contentTypeName;
        return typeCode != null ? typeCode.getCodeName() : null;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.deletedYn == null) {
            this.deletedYn = "N";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public ContentDTO toDTO() {
        return ContentDTO.builder()
                .contentId(this.contentId)
                .contentType(this.contentTypeCode)
                .contentTypeName(this.getContentTypeName())
                .subject(this.subject)
                .contentBody(this.contentBody)
                .urlInfo(this.urlInfo)
                .createdBy(this.createdBy)
                .createdByName(this.getCreatedByName())
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}

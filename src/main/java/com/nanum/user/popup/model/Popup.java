package com.nanum.user.popup.model;

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
@Table(name = "popup")
public class Popup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private int popupId;

    @Column(name = "title")
    private String title;

    @Column(name = "content_image")
    private String contentImage;

    @Column(name = "content_html")
    private String contentHtml; // TEXT/HTML

    @Column(name = "link_type")
    private String linkType;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "pos_x")
    private int posX;

    @Column(name = "pos_y")
    private int posY;

    @Column(name = "close_type")
    private String closeType; // NEVER, DAY, ONCE

    @Column(name = "device_type")
    private String deviceType; // PC, MOBILE, ALL

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "delete_yn", insertable = false)
    private String deleteYn;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "created_by", insertable = false, updatable = false)
    @lombok.ToString.Exclude
    private com.nanum.user.member.model.Member creator;

    // 조인
    @Transient
    private String creatorName;

    public String getCreatorName() {
        if (creatorName != null)
            return creatorName;
        return creator != null ? creator.getMemberName() : null;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.deleteYn == null) {
            this.deleteYn = "N";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public PopupDTO toDTO() {
        return PopupDTO.builder()
                .popupId(this.popupId)
                .title(this.title)
                .contentImage(this.contentImage)
                .contentHtml(this.contentHtml)
                .linkType(this.linkType)
                .linkUrl(this.linkUrl)
                .width(this.width)
                .height(this.height)
                .posX(this.posX)
                .posY(this.posY)
                .closeType(this.closeType)
                .deviceType(this.deviceType)
                .startDatetime(this.startDatetime)
                .endDatetime(this.endDatetime)
                .useYn(this.useYn)
                .creatorName(this.getCreatorName())
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}

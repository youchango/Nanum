package com.nanum.user.banner.model;

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
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private int bannerId;

    @Column(name = "title")
    private String title;

    @Column(name = "banner_type")
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private BannerType bannerType; // MAIN_TOP, SUB_MID

    @Column(name = "image_file")
    private String imageFile;

    @Column(name = "link_type")
    private String linkType;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "device_type")
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private DeviceType deviceType; // PC, MOBILE, ALL

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
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

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

    public BannerDTO toDTO() {
        return BannerDTO.builder()
                .bannerId(this.bannerId)
                .bannerType(this.bannerType)
                .imageFile(null) // MultipartFile은 변환 대상 제외
                .linkType(this.linkType)
                .linkUrl(this.linkUrl)
                .sortOrder(this.sortOrder)
                .deviceType(this.deviceType)
                .startDatetime(this.startDatetime)
                .endDatetime(this.endDatetime)
                .useYn(this.useYn)
                .keyword(null)
                .build();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

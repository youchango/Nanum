package com.nanum.domain.banner.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE banner SET delete_yn = 'Y', deleted_at = NOW() WHERE banner_id = ?")
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long id;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "banner_type", nullable = false)
    private BannerType type;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "sort_order", nullable = false)
    @ColumnDefault("1")
    private int sortOrder;

    // device_type VARCHAR(10) DEFAULT 'ALL'
    @Column(name = "device_type", nullable = false)
    @ColumnDefault("'ALL'")
    @Builder.Default
    private String deviceType = "ALL";

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "use_yn", nullable = false)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String useYn = "Y";

    public void update(BannerType type, String linkUrl, int sortOrder, String useYn,
            LocalDateTime start, LocalDateTime end) {
        this.type = type;
        this.linkUrl = linkUrl;
        this.sortOrder = sortOrder;
        this.useYn = useYn;
        this.startDatetime = start;
        this.endDatetime = end;
    }
}

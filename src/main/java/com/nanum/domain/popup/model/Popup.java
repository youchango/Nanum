package com.nanum.domain.popup.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "popup")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE popup SET delete_yn = 'Y', deleted_at = NOW() WHERE popup_id = ?")
public class Popup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long id;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Column(nullable = false)
    private String title;

    @Column(name = "content_html")
    private String contentHtml;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(nullable = false)
    @ColumnDefault("400")
    private int width;

    @Column(nullable = false)
    @ColumnDefault("500")
    private int height;

    @Column(name = "pos_x", nullable = false)
    @ColumnDefault("0")
    private int posX;

    @Column(name = "pos_y", nullable = false)
    @ColumnDefault("0")
    private int posY;

    @Enumerated(EnumType.STRING)
    @Column(name = "close_type", nullable = false)
    @Builder.Default
    private PopupCloseType closeType = PopupCloseType.DAY;

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

    public void update(String title, String linkUrl, int width, int height, int posX, int posY,
            PopupCloseType closeType, LocalDateTime start, LocalDateTime end, String useYn) {
        this.title = title;
        this.linkUrl = linkUrl;
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.closeType = closeType;
        this.startDatetime = start;
        this.endDatetime = end;
        this.useYn = useYn;
    }
}

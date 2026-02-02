package com.nanum.user.banner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private Integer bannerId;
    private BannerType bannerType;
    private String linkType;
    private String linkUrl;
    private Integer sortOrder;
    private DeviceType deviceType;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private java.time.LocalDateTime startDatetime;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private java.time.LocalDateTime endDatetime;

    // 호환성을 위해 유지하거나 필요 시 제거
    private String startDatetimeStr;
    private String endDatetimeStr;
    private String useYn;

    private MultipartFile imageFile; // 업로드용

    private String keyword;

    public Banner toEntity() {
        Banner banner = new Banner();
        banner.setBannerType(this.bannerType);
        banner.setLinkType(this.linkType);
        banner.setLinkUrl(this.linkUrl);
        banner.setSortOrder(this.sortOrder);
        banner.setDeviceType(this.deviceType);
        banner.setUseYn(this.useYn);
        banner.setStartDatetime(this.startDatetime);
        banner.setEndDatetime(this.endDatetime);
        return banner;
    }

    public void updateEntity(Banner banner) {
        banner.setBannerType(this.bannerType);
        banner.setLinkType(this.linkType);
        banner.setLinkUrl(this.linkUrl);
        banner.setSortOrder(this.sortOrder);
        banner.setDeviceType(this.deviceType);
        banner.setUseYn(this.useYn);
        banner.setStartDatetime(this.startDatetime);
        banner.setEndDatetime(this.endDatetime);
    }
}

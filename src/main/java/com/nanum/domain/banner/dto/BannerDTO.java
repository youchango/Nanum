package com.nanum.domain.banner.dto;

import com.nanum.domain.banner.model.Banner;
import com.nanum.domain.banner.model.BannerType;
import lombok.*;

import java.time.LocalDateTime;

public class BannerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private BannerType type;
        private String imageFile;
        private String linkUrl;
        private int sortOrder;
        private String deviceType;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private String useYn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private BannerType type;
        private String imageFile;
        private String linkUrl;
        private int sortOrder;
        private String deviceType;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private String useYn;

        public static Response from(Banner banner) {
            return Response.builder()
                    .id(banner.getId())
                    .type(banner.getType())
                    .imageFile(banner.getImageFile())
                    .linkUrl(banner.getLinkUrl())
                    .sortOrder(banner.getSortOrder())
                    .deviceType(banner.getDeviceType())
                    .startDatetime(banner.getStartDatetime())
                    .endDatetime(banner.getEndDatetime())
                    .useYn(banner.getUseYn())
                    .build();
        }
    }
}

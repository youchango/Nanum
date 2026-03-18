package com.nanum.domain.banner.dto;

import com.nanum.domain.banner.model.Banner;
import com.nanum.domain.banner.model.BannerType;
import lombok.*;

import java.time.LocalDateTime;

import com.nanum.domain.file.dto.FileResponseDTO;
import java.util.List;

public class BannerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private BannerType type;
        private String siteCd;
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
        private String title;
        private String siteCd;
        private BannerType type;
        private String linkUrl;
        private int sortOrder;
        private String deviceType;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private String useYn;
        private List<FileResponseDTO> files;

        public static Response from(Banner banner) {
            return Response.builder()
                    .id(banner.getId())
                    .title(banner.getTitle())
                    .siteCd(banner.getSiteCd())
                    .type(banner.getType())
                    .linkUrl(banner.getLinkUrl())
                    .sortOrder(banner.getSortOrder())
                    .deviceType(banner.getDeviceType())
                    .startDatetime(banner.getStartDatetime())
                    .endDatetime(banner.getEndDatetime())
                    .useYn(banner.getUseYn())
                    // Files should be set separately as DTO doesn't have access to FileService here
                    // or we can expect them to be set by the service
                    .build();
        }
    }
}

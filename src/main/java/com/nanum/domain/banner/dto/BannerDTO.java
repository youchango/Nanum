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
        private LocalDateTime createdAt;
        /** 대표 이미지 전체 접근 URL (FileService.getFullUrl로 설정) */
        private String imageUrl;
        /** 파일 목록 (전체 파일 메타데이터) */
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
                    .createdAt(banner.getCreatedAt())
                    .build();
        }
    }
}

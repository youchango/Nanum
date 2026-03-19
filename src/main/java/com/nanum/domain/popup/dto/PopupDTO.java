package com.nanum.domain.popup.dto;

import com.nanum.domain.popup.model.Popup;
import com.nanum.domain.popup.model.PopupCloseType;
import lombok.*;

import java.time.LocalDateTime;

import com.nanum.domain.file.dto.FileResponseDTO;
import java.util.List;

public class PopupDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String siteCd;          // 사이트 코드 (등록/수정 시 필수)
        private String contentHtml;
        private String linkUrl;
        private int width;
        private int height;
        private int posX;
        private int posY;
        private PopupCloseType closeType;
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
        private String siteCd;
        private String title;
        private String contentHtml;
        private String linkUrl;
        private int width;
        private int height;
        private int posX;
        private int posY;
        private PopupCloseType closeType;
        private String deviceType;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private String useYn;
        /** 대표 이미지 전체 접근 URL (FileService.getFullUrl로 설정) */
        private String imageUrl;
        /** 파일 목록 (전체 파일 메타데이터) */
        private List<FileResponseDTO> files;

        public static Response from(Popup popup) {
            return Response.builder()
                    .id(popup.getId())
                    .siteCd(popup.getSiteCd())
                    .title(popup.getTitle())
                    .contentHtml(popup.getContentHtml())
                    .linkUrl(popup.getLinkUrl())
                    .width(popup.getWidth())
                    .height(popup.getHeight())
                    .posX(popup.getPosX())
                    .posY(popup.getPosY())
                    .closeType(popup.getCloseType())
                    .deviceType(popup.getDeviceType())
                    .startDatetime(popup.getStartDatetime())
                    .endDatetime(popup.getEndDatetime())
                    .useYn(popup.getUseYn())
                    .build();
        }
    }
}

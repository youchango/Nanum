package com.nanum.admin.manager.dto;

import com.nanum.admin.manager.entity.ManagerAuthGroup;
import lombok.*;

import java.time.LocalDateTime;

public class ManagerAuthGroupDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Info {
        private Long authGroupSeq;
        private String authGroupName;
        private String useYn;
        private String createdBy;
        private LocalDateTime createdAt;
        private String updatedBy;
        private LocalDateTime updatedAt;

        public static Info from(ManagerAuthGroup entity) {
            return Info.builder()
                    .authGroupSeq(entity.getAuthGroupSeq())
                    .authGroupName(entity.getAuthGroupName())
                    .useYn(entity.getUseYn())
                    .createdBy(entity.getCreatedBy())
                    .createdAt(entity.getCreatedAt())
                    .updatedBy(entity.getUpdatedBy())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String authGroupName;
        private String useYn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private Long authGroupSeq;
        private String authGroupName;
        private String useYn;
    }
}

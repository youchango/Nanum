package com.nanum.admin.manager.dto;

import com.nanum.admin.manager.entity.Manager;
import lombok.*;

import java.time.LocalDateTime;

public class ManagerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        private String id;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private ManagerInfo manager;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagerInfo {
        private Long seq;
        private String id;
        private String name;
        private String email;
        private String type; // MASTER, SCM, ADMIN
        private Integer authGroupSeq;
        private LocalDateTime lastLoginDate;
        private String siteCd;
        private String applyYn;

        public static ManagerInfo from(Manager manager) {
            return ManagerInfo.builder()
                    .seq(manager.getManagerSeq())
                    .id(manager.getManagerId())
                    .name(manager.getManagerName())
                    .email(manager.getManagerEmail())
                    .type(manager.getMbType())
                    .authGroupSeq(manager.getAuthGroupSeq())
                    .lastLoginDate(manager.getLoginDate())
                    .siteCd(manager.getSiteCd())
                    .applyYn(manager.getApplyYn())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String id;
        private String password;
        private String name;
        private String email;
        private Integer authGroupSeq;
        private String type;
        private String description;
        private String siteCd;
    }
}

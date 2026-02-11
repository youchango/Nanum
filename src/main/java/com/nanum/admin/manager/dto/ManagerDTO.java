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
        private Long authGroupSeq;
        private LocalDateTime lastLoginDate;
        private String siteCd;
        private String applyYn;
        private ManagerScmDTO.Info scmInfo;
        private String managerCode; // Added for consistency

        public static ManagerInfo from(Manager manager) {
            return ManagerInfo.builder()
                    .seq(manager.getManagerSeq())
                    .id(manager.getManagerId())
                    .name(manager.getManagerName())
                    .email(manager.getManagerEmail())
                    .type(manager.getMbType())
                    .authGroupSeq(manager.getAuthGroup() != null ? manager.getAuthGroup().getAuthGroupSeq() : null)
                    .lastLoginDate(manager.getLoginDate())
                    .siteCd(manager.getSiteCd())
                    .applyYn(manager.getApplyYn())
                    .scmInfo(manager.getManagerScm() != null ? ManagerScmDTO.Info.from(manager.getManagerScm()) : null)
                    .managerCode(manager.getManagerCode()) // Map managerCode
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
        private Long authGroupSeq;
        private String type;
        private String description;
        private String siteCd;
        private ManagerScmDTO.Info scmInfo;
    }
}

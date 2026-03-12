package com.nanum.admin.manager.dto;

import com.nanum.admin.manager.entity.ManagerMenu;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerMenuDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Info {
        private Long menuSeq;
        private Long parentMenuSeq;
        private String menuName;
        private String menuUrl;
        private String displayYn;
        private Integer displayOrder;
        private String menuParameter;
        private List<Info> children;

        public static Info from(ManagerMenu entity) {
            return Info.builder()
                    .menuSeq(entity.getMenuSeq())
                    .parentMenuSeq(entity.getParent() != null ? entity.getParent().getMenuSeq() : null)
                    .menuName(entity.getMenuName())
                    .menuUrl(entity.getMenuUrl())
                    .displayYn(entity.getDisplayYn())
                    .displayOrder(entity.getDisplayOrder())
                    .menuParameter(entity.getMenuParameter())
                    .children(entity.getChildren().stream().map(Info::from).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private Long parentMenuSeq;
        private String menuName;
        private String menuUrl;
        private String displayYn;
        private Integer displayOrder;
        private String menuParameter;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private Long menuSeq;
        private Long parentMenuSeq;
        private String menuName;
        private String menuUrl;
        private String displayYn;
        private Integer displayOrder;
        private String menuParameter;
    }
}

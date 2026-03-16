package com.nanum.admin.inout.dto;

import lombok.*;

import java.util.List;

/**
 * 출고 등록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboundRequest {
    private String ioCategory;
    private String managerCode;
    private List<OutboundItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OutboundItem {
        private String inIoCode;
        private Integer inNo;
        private Long productId;
        private String productName;
        private Long optionId;
        private String optionName;
        private String brandName;
        private Integer qty;
        private String memo;
    }
}

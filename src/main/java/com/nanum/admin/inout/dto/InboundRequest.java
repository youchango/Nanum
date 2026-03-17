package com.nanum.admin.inout.dto;

import lombok.*;

import java.util.List;

/**
 * 입고 등록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundRequest {
    private String ioCategory;
    private String managerCode;
    private List<InboundItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InboundItem {
        private Long productId;
        private String productName;
        private Long optionId;
        private String optionName;
        private String brandName;
        private Integer qty;
        private String memo;
    }
}

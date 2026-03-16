package com.nanum.admin.inout.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 입출고 현황 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InoutStatusResponse {
    private Long rowNum;
    private String ioCode;
    private String ioType;
    private String ioCategory;
    private String brandName;
    private String productName;
    private String supplierName;
    private LocalDateTime createdAt;
}

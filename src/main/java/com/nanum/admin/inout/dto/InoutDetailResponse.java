package com.nanum.admin.inout.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 입출고 상세 내역 응답 DTO (입고/출고 관리용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InoutDetailResponse {
    private Long rowNum;
    private String ioCode;
    private Integer no;
    private String displayIoCode; // <io_code + " - " + no>
    private String ioType;
    private String ioCategory;
    private String supplierName;
    private String productName;
    private String optionName;
    private String brandName;
    private Integer qty;
    private Integer realQty;
    private String memo;
    private String inIoCode;
    private Integer inNo;
    private String displayInIoCode; // <in_io_code + " - " + in_no>
    private LocalDateTime createdAt;
}

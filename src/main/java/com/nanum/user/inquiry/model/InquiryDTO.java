package com.nanum.user.inquiry.model;

import lombok.Data;

@Data
public class InquiryDTO {
    private int inquiryId;
    private int inquiryTypeCode;
    private String title;
    private String content;
    private String answer;
    private InquiryStatus inquiryStatus; // WAIT, ANSWERED

    // 검색용
    private String searchType;
    private String keyword;
}

package com.nanum.domain.inquiry.dto;

import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.model.InquiryStatus;
import com.nanum.domain.inquiry.model.InquiryType;
import lombok.*;

import java.time.LocalDateTime;

public class InquiryDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private InquiryType type;
        private Long productId;
        private String orderNo;
        private String title;
        private String content;
        private String isSecret;
        private String siteCd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReplyRequest {
        private String answer;
        // answererCode is extracted from JWT
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Search {
        private InquiryType type;
        private InquiryStatus status;
        private Long productId;
        private String orderNo;
        private String keyword; // Title or Content or Writer
        private String writerCode;
        private String siteCd;
        @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
        private java.time.LocalDate startDate;
        @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
        private java.time.LocalDate endDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private InquiryType type;
        
        public String getTypeDesc() {
            return type != null ? type.getDescription() : null;
        }

        private Long productId;
        private String orderNo;
        private String title;
        private String content;
        private String answer;
        private InquiryStatus status;

        public String getStatusDesc() {
            return status != null ? status.getDescription() : null;
        }

        private String writerCode;
        private String writerName;
        private String siteCd;
        private String shopName;
        private String productName;
        private String orderName;
        private String isSecret;
        private LocalDateTime createdAt;
        private LocalDateTime answeredAt;
        private String answererCode;

        public static Response from(Inquiry inquiry) {
            return Response.builder()
                    .id(inquiry.getId())
                    .type(inquiry.getType())
                    .productId(inquiry.getProductId())
                    .orderNo(inquiry.getOrderNo())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .answer(inquiry.getAnswer())
                    .status(inquiry.getStatus())
                    .writerCode(inquiry.getWriter().getMemberCode())
                    .writerName(inquiry.getWriter().getMemberName())
                    .siteCd(inquiry.getSiteCd())
                    .shopName(null) // Service에서 보정
                    .isSecret(inquiry.getIsSecret())
                    .createdAt(inquiry.getCreatedAt())
                    .answeredAt(inquiry.getAnsweredAt())
                    .answererCode(inquiry.getAnswerer() != null ? inquiry.getAnswerer().getManagerCode() : null)
                    .build();
        }
    }
}

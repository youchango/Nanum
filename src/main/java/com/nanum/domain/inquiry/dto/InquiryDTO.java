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
        private String title;
        private String content;
        // writerCode is extracted from JWT
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
        private String keyword; // Title or Content or Writer
        private String writerCode;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private InquiryType type;
        private String typeDesc;
        private String title;
        private String content;
        private String answer;
        private InquiryStatus status;
        private String statusDesc;
        private String writerCode;
        private String writerName; // Optional if Member has name
        private LocalDateTime createdAt;
        private LocalDateTime answeredAt;
        private String answererCode;

        public static Response from(Inquiry inquiry) {
            return Response.builder()
                    .id(inquiry.getId())
                    .type(inquiry.getType())
                    .typeDesc(inquiry.getType().getDescription())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .answer(inquiry.getAnswer())
                    .status(inquiry.getStatus())
                    .statusDesc(inquiry.getStatus().getDescription())
                    .writerCode(inquiry.getWriter().getMemberCode()) // Assuming Member has getMemberCode
                    // .writerName(inquiry.getWriter().getName())
                    .createdAt(inquiry.getCreatedAt())
                    .answeredAt(inquiry.getAnsweredAt())
                    .answererCode(inquiry.getAnswerer() != null ? inquiry.getAnswerer().getMemberCode() : null)
                    .build();
        }
    }
}

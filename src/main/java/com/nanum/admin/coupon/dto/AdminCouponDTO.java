package com.nanum.admin.coupon.dto;

import com.nanum.domain.coupon.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class AdminCouponDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String siteCd;
        private String name;
        private String discountType; // FIXED, RATE
        private Integer discountValue;
        private Integer maxDiscount;
        private Integer minOrderPrice;
        private LocalDateTime validStartDate;
        private LocalDateTime validEndDate;
        private String targetMemberType; // ALL, U, B, V
        private Long targetProductId;
        private Integer issueLimit;
        private Integer memberIssueLimit;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String siteCd;
        private String name;
        private String discountType;
        private Integer discountValue;
        private Integer maxDiscount;
        private Integer minOrderPrice;
        private LocalDateTime validStartDate;
        private LocalDateTime validEndDate;
        private String targetMemberType;
        private Long targetProductId;
        private Integer issueLimit;
        private Integer memberIssueLimit;
        private Integer issueCount;
        private LocalDateTime createdAt;
        private Long createdBy;

        public static Response from(Coupon coupon) {
            return Response.builder()
                    .id(coupon.getId())
                    .siteCd(coupon.getSiteCd())
                    .name(coupon.getName())
                    .discountType(coupon.getDiscountType())
                    .discountValue(coupon.getDiscountValue())
                    .maxDiscount(coupon.getMaxDiscount())
                    .minOrderPrice(coupon.getMinOrderPrice())
                    .validStartDate(coupon.getValidStartDate())
                    .validEndDate(coupon.getValidEndDate())
                    .targetMemberType(coupon.getTargetMemberType())
                    .targetProductId(coupon.getTargetProductId())
                    .issueLimit(coupon.getIssueLimit())
                    .memberIssueLimit(coupon.getMemberIssueLimit())
                    .issueCount(coupon.getIssueCount())
                    .createdAt(coupon.getCreatedAt())
                    .createdBy(coupon.getCreatedBy())
                    .build();
        }
    }

    @Getter
    @Setter
    public static class Search {
        private String siteCd;
        private String searchTargetMemberType; // ALL, U, B, V
        private String searchKeyword; // name search
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssueRequest {
        private List<String> memberCodes; // 수동으로 선택한 회원 코드 목록
        private boolean bulkIssueByGrade; // true면 targetMemberType에 해당하는 전 등급 회원에게 일괄 발급
    }
}

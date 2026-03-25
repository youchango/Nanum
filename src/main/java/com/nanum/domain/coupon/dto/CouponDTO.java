package com.nanum.domain.coupon.dto;

import com.nanum.domain.coupon.model.Coupon;
import com.nanum.domain.coupon.model.MemberCoupon;
import lombok.*;

import java.time.LocalDateTime;

public class CouponDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long memberCouponId;
        private String couponName;
        private String discountType;
        private Integer discountValue;
        private Integer maxDiscount;
        private Integer minOrderPrice;
        private LocalDateTime validStartDate;
        private LocalDateTime validEndDate;
        private String status;

        public static Response from(MemberCoupon mc) {
            Coupon coupon = mc.getCoupon();
            return Response.builder()
                    .memberCouponId(mc.getIssueId())
                    .couponName(coupon.getName())
                    .discountType(coupon.getDiscountType())
                    .discountValue(coupon.getDiscountValue())
                    .maxDiscount(coupon.getMaxDiscount())
                    .minOrderPrice(coupon.getMinOrderPrice())
                    .validStartDate(coupon.getValidStartDate())
                    .validEndDate(coupon.getValidEndDate())
                    .status(mc.getStatus().name())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DownloadableResponse {
        private Long couponId;
        private String couponName;
        private String discountType;
        private Integer discountValue;
        private Integer maxDiscount;
        private Integer minOrderPrice;
        private LocalDateTime validStartDate;
        private LocalDateTime validEndDate;
        private Integer issueLimit;
        private Integer issueCount;
        private Integer memberIssueLimit;
        private boolean canDownload;

        public static DownloadableResponse from(Coupon coupon) {
            return DownloadableResponse.builder()
                    .couponId(coupon.getId())
                    .couponName(coupon.getName())
                    .discountType(coupon.getDiscountType())
                    .discountValue(coupon.getDiscountValue())
                    .maxDiscount(coupon.getMaxDiscount())
                    .minOrderPrice(coupon.getMinOrderPrice())
                    .validStartDate(coupon.getValidStartDate())
                    .validEndDate(coupon.getValidEndDate())
                    .issueLimit(coupon.getIssueLimit())
                    .issueCount(coupon.getIssueCount())
                    .memberIssueLimit(coupon.getMemberIssueLimit())
                    .build();
        }
    }
}

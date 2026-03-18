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
        private String usedYn;

        public static Response from(MemberCoupon mc) {
            Coupon coupon = mc.getCoupon();
            return Response.builder()
                    .memberCouponId(mc.getId())
                    .couponName(coupon.getName())
                    .discountType(coupon.getDiscountType())
                    .discountValue(coupon.getDiscountValue())
                    .maxDiscount(coupon.getMaxDiscount())
                    .minOrderPrice(coupon.getMinOrderPrice())
                    .validStartDate(coupon.getValidStartDate())
                    .validEndDate(coupon.getValidEndDate())
                    .usedYn(mc.getUsedYn())
                    .build();
        }
    }
}

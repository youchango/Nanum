package com.nanum.admin.coupon.controller;

import com.nanum.admin.coupon.dto.AdminCouponDTO;
import com.nanum.admin.coupon.service.AdminCouponService;
import com.nanum.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Coupon", description = "관리자 쿠폰 관리 API")
@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @Operation(summary = "전체 쿠폰 정책 조회", description = "검색 조건에 맞는 쿠폰 마스터 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminCouponDTO.Response>>> getCoupons(@ModelAttribute AdminCouponDTO.Search search) {
        List<AdminCouponDTO.Response> list = adminCouponService.getCoupons(search);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "신규 쿠폰 마스터 생성", description = "회원 혜택용 신규 쿠폰 정책을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createCoupon(@RequestBody AdminCouponDTO.Request request) {
        Long couponId = adminCouponService.createCoupon(request);
        return ResponseEntity.ok(ApiResponse.success(couponId));
    }

    @Operation(summary = "쿠폰 다중/일괄 발급", description = "선택한 회원(들) 또는 타겟 등급 전체에 쿠폰을 발급합니다.")
    @PostMapping("/{couponId}/issue")
    public ResponseEntity<ApiResponse<Void>> issueCoupon(
            @PathVariable Long couponId,
            @RequestBody AdminCouponDTO.IssueRequest issueRequest) {
        adminCouponService.issueCoupon(couponId, issueRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

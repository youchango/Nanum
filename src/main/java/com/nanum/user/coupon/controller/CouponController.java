package com.nanum.user.coupon.controller;

import com.nanum.domain.coupon.dto.CouponDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Coupon", description = "Coupon API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "사용 가능한 쿠폰 조회", description = "현재 로그인한 사용자의 사용 가능한 쿠폰 목록(미사용, 유효기간 내)을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponDTO.Response>>> getAvailableCoupons(
            Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        Page<CouponDTO.Response> coupons = couponService.getAvailableCoupons(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 쿠폰 조회 성공", coupons));
    }

    @Operation(summary = "전체 쿠폰 조회", description = "현재 로그인한 사용자의 전체 쿠폰 목록(사용 완료 포함)을 페이징하여 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<CouponDTO.Response>>> getAllCoupons(
            Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        Page<CouponDTO.Response> coupons = couponService.getAllCoupons(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("전체 쿠폰 조회 성공", coupons));
    }
}

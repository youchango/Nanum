package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminBannerService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.banner.dto.BannerDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final AdminBannerService adminBannerService;

    @GetMapping
    @Operation(summary = "배너 목록 조회")
    public ApiResponse<List<BannerDTO.Response>> getBanners() {
        return ApiResponse.success(adminBannerService.getBanners());
    }

    @PostMapping
    @Operation(summary = "배너 등록")
    public ApiResponse<Long> createBanner(@RequestBody BannerDTO.Request request) {
        return ApiResponse.success(adminBannerService.createBanner(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "배너 수정")
    public ApiResponse<Void> updateBanner(@PathVariable Long id, @RequestBody BannerDTO.Request request) {
        adminBannerService.updateBanner(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제")
    public ApiResponse<Void> deleteBanner(@PathVariable Long id) {
        adminBannerService.deleteBanner(id);
        return ApiResponse.success(null);
    }
}

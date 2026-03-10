package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminBannerService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.banner.dto.BannerDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminBanner", description = "AdminBanner API")
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "배너 등록")
    public ApiResponse<Long> createBanner(
            @RequestPart(value = "request") BannerDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ApiResponse.success(adminBannerService.createBanner(request, files));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "배너 수정")
    public ApiResponse<Void> updateBanner(
            @PathVariable Long id,
            @RequestPart(value = "request") BannerDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        adminBannerService.updateBanner(id, request, files);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제")
    public ApiResponse<Void> deleteBanner(@PathVariable Long id) {
        // TODO: Get actual logged-in member code
        String memberCode = "ADMIN";
        adminBannerService.deleteBanner(id, memberCode);
        return ApiResponse.success(null);
    }
}

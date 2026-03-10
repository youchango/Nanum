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
    @Operation(summary = "배너 목록 조회", description = "데이터베이스의 banner 테이블에서 현재 등록된 모든 배너 목록을 조회하여 반환합니다.")
    public ApiResponse<List<BannerDTO.Response>> getBanners() {
        return ApiResponse.success(adminBannerService.getBanners());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "배너 등록", description = "배너 정보(BannerDTO.Request)와 이미지 파일들을 전달받아 데이터베이스에 신규 배너를 등록합니다.")
    public ApiResponse<Long> createBanner(
            @RequestPart(value = "request") BannerDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ApiResponse.success(adminBannerService.createBanner(request, files));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "배너 수정", description = "특정 배너 ID를 식별자로 하여, 전달받은 수정 요청 데이터와 파일로 기존 배너 정보를 업데이트합니다.")
    public ApiResponse<Void> updateBanner(
            @PathVariable Long id,
            @RequestPart(value = "request") BannerDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        adminBannerService.updateBanner(id, request, files);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제", description = "특정 배너 ID를 식별자로 하여, 해당 배너를 데이터베이스에서 논리적으로 삭제(비활성화) 처리합니다.")
    public ApiResponse<Void> deleteBanner(@PathVariable Long id) {
        // TODO: Get actual logged-in member code
        String memberCode = "ADMIN";
        adminBannerService.deleteBanner(id, memberCode);
        return ApiResponse.success(null);
    }
}

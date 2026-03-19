package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminBannerService;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.banner.dto.BannerDTO;
import com.nanum.domain.banner.dto.BannerSearchDTO;
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

    /**
     * 배너 목록 조회 (검색 및 페이지네이션 지원).
     * @param searchDTO 검색 조건 (siteCd, searchUseYn, searchStartDate, searchEndDate, searchType, searchKeyword, page, size)
     * @return 검색된 배너 목록
     */
    @GetMapping
    @Operation(summary = "배너 목록 조회", description = "검색 조건(기간, 사용여부, 키워드, 사이트코드)으로 배너 목록을 조회합니다.")
    public ApiResponse<List<BannerDTO.Response>> getBanners(@ModelAttribute BannerSearchDTO searchDTO) {
        return ApiResponse.success(adminBannerService.getBanners(searchDTO));
    }

    /**
     * 배너 등록.
     * @param request   배너 등록 요청 DTO
     * @param files     이미지 파일 목록 (선택)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "배너 등록", description = "배너 정보(BannerDTO.Request)와 이미지 파일들을 전달받아 데이터베이스에 신규 배너를 등록합니다.")
    public ApiResponse<Long> createBanner(
            @ModelAttribute BannerDTO.Request request,
            @RequestParam(value = "imageFile", required = false) List<MultipartFile> files) {
        return ApiResponse.success(adminBannerService.createBanner(request, files));
    }

    /**
     * 배너 수정.
     * @param id        배너 ID
     * @param request   배너 수정 요청 DTO
     * @param files     이미지 파일 목록 (선택)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "배너 수정", description = "특정 배너 ID를 식별자로 하여, 전달받은 수정 요청 데이터와 파일로 기존 배너 정보를 업데이트합니다.")
    public ApiResponse<Void> updateBanner(
            @PathVariable Long id,
            @ModelAttribute BannerDTO.Request request,
            @RequestParam(value = "imageFile", required = false) List<MultipartFile> files) {
        adminBannerService.updateBanner(id, request, files);
        return ApiResponse.success(null);
    }

    /**
     * 배너 삭제 (논리 삭제).
     * @param id 배너 ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제", description = "특정 배너 ID를 식별자로 하여, 해당 배너를 데이터베이스에서 논리적으로 삭제(비활성화) 처리합니다.")
    public ApiResponse<Void> deleteBanner(@PathVariable Long id) {
        String memberCode = ManagerType.ADMIN.name();
        adminBannerService.deleteBanner(id, memberCode);
        return ApiResponse.success(null);
    }
}

package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.banner.dto.BannerDTO;
import com.nanum.domain.banner.model.BannerType;
import com.nanum.user.management.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Banner", description = "Banner API")
@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "배너 목록 조회")
    public ApiResponse<List<BannerDTO.Response>> getBanners(@RequestParam BannerType type) {
        return ApiResponse.success(bannerService.getBanners(type));
    }
}

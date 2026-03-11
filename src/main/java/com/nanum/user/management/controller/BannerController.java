package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.banner.dto.BannerDTO;
import com.nanum.domain.banner.model.BannerType;
import com.nanum.user.management.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class BannerController implements ResponseSupport {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "배너 목록 조회", description = "배너 유형(BannerType)과 사이트 코드(site_cd)에 따라 동적으로 활성화된 배너를 조회합니다.")
    public ResponseEntity<ApiResponse<List<BannerDTO.Response>>> getBanners(
            @RequestParam BannerType type,
            @RequestParam(name = "site_cd") String siteCd) {
        return success(bannerService.getBanners(type, siteCd));
    }
}

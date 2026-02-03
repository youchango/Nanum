package com.nanum.admin.banner.controller;

import com.nanum.admin.banner.service.AdminBannerService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.global.security.CustomUserDetails;
import com.nanum.user.banner.model.Banner;
import com.nanum.user.banner.model.BannerDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Admin Banner", description = "관리자 배너 관리 API")
@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final AdminBannerService adminBannerService;

    @Operation(summary = "배너 목록 조회", description = "배너 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(@ModelAttribute SearchDTO searchDTO) {
        if (searchDTO.getPage() == 0)
            searchDTO.setPage(1);

        List<Banner> bannerList = adminBannerService.getBannerList(searchDTO);
        int totalCount = adminBannerService.getBannerCount(searchDTO);

        // Entity -> DTO 변환
        List<BannerDTO> bannerDTOList = bannerList.stream()
                .map(Banner::toDTO)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("bannerList", bannerDTOList);
        responseData.put("totalCount", totalCount);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @Operation(summary = "배너 상세 조회", description = "배너 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerDTO>> detail(@PathVariable int id) {
        Banner banner = adminBannerService.getBannerDetail(id);
        if (banner == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(banner.toDTO()));
    }

    @Operation(summary = "배너 등록", description = "새로운 배너를 등록합니다. (Multipart Request)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> create(@ModelAttribute BannerDTO bannerDTO,
            org.springframework.validation.BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("배너 등록 요청 - BannerDTO: {}", bannerDTO);
        if (bindingResult.hasErrors()) {
            log.error("배너 등록 바인딩 에러: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            if (bannerDTO.getImageFile() != null) {
                log.info("수신된 파일명: {}, 크기: {}", bannerDTO.getImageFile().getOriginalFilename(),
                        bannerDTO.getImageFile().getSize());
            } else {
                log.warn("수신된 파일이 없습니다 (null)");
            }
            String createdBy = userDetails.getMember().getMemberCode();
            adminBannerService.registerBanner(bannerDTO, createdBy);
            return ResponseEntity.ok(ApiResponse.success("배너가 등록되었습니다.", null));
        } catch (IOException e) {
            log.error("배너 등록 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "배너 수정", description = "배너 정보를 수정합니다. (Multipart Request)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable int id,
            @ModelAttribute BannerDTO bannerDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String updatedBy = userDetails.getMember().getMemberCode();
            bannerDTO.setBannerId(id);
            adminBannerService.updateBanner(bannerDTO, updatedBy);
            return ResponseEntity.ok(ApiResponse.success("배너가 수정되었습니다.", null));
        } catch (IOException e) {
            log.error("배너 수정 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "배너 삭제", description = "배너를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable int id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String deletedBy = userDetails.getMember().getMemberCode();
        adminBannerService.deleteBanner(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("배너가 삭제되었습니다.", null));
    }
}

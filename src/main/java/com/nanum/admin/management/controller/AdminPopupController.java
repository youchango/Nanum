package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminPopupService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.popup.dto.PopupDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminPopup", description = "AdminPopup API")
@RestController
@RequestMapping("/api/v1/admin/popups")
@RequiredArgsConstructor
public class AdminPopupController {

    private final AdminPopupService adminPopupService;

    @GetMapping
    @Operation(summary = "팝업 목록 조회", description = "데이터베이스의 popup 테이블에서 현재 등록된 모든 팝업 목록을 조회하여 반환합니다.")
    public ApiResponse<List<PopupDTO.Response>> getPopups() {
        return ApiResponse.success(adminPopupService.getPopups());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업 등록", description = "팝업 정보(PopupDTO.Request)와 이미지 파일들을 전달받아 데이터베이스에 신규 팝업을 등록합니다.")
    public ApiResponse<Long> createPopup(
            @RequestPart(value = "request") PopupDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ApiResponse.success(adminPopupService.createPopup(request, files));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업 수정", description = "특정 팝업 ID를 식별자로 하여, 전달받은 수정 요청 데이터와 파일로 기존 팝업 정보를 업데이트합니다.")
    public ApiResponse<Void> updatePopup(
            @PathVariable Long id,
            @RequestPart(value = "request") PopupDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        adminPopupService.updatePopup(id, request, files);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "팝업 삭제", description = "특정 팝업 ID를 식별자로 하여, 해당 팝업을 데이터베이스에서 논리적으로 삭제 처리합니다.")
    public ApiResponse<Void> deletePopup(@PathVariable Long id) {
        // TODO: Get actual logged-in member code
        String memberCode = "ADMIN";
        adminPopupService.deletePopup(id, memberCode);
        return ApiResponse.success(null);
    }
}

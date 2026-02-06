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

@RestController
@RequestMapping("/api/v1/admin/popups")
@RequiredArgsConstructor
public class AdminPopupController {

    private final AdminPopupService adminPopupService;

    @GetMapping
    @Operation(summary = "팝업 목록 조회")
    public ApiResponse<List<PopupDTO.Response>> getPopups() {
        return ApiResponse.success(adminPopupService.getPopups());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업 등록")
    public ApiResponse<Long> createPopup(
            @RequestPart(value = "request") PopupDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ApiResponse.success(adminPopupService.createPopup(request, files));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업 수정")
    public ApiResponse<Void> updatePopup(
            @PathVariable Long id,
            @RequestPart(value = "request") PopupDTO.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        adminPopupService.updatePopup(id, request, files);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "팝업 삭제")
    public ApiResponse<Void> deletePopup(@PathVariable Long id) {
        // TODO: Get actual logged-in member code
        String memberCode = "ADMIN";
        adminPopupService.deletePopup(id, memberCode);
        return ApiResponse.success(null);
    }
}

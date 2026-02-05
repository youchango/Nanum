package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminPopupService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.popup.dto.PopupDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @Operation(summary = "팝업 등록")
    public ApiResponse<Long> createPopup(@RequestBody PopupDTO.Request request) {
        return ApiResponse.success(adminPopupService.createPopup(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "팝업 수정")
    public ApiResponse<Void> updatePopup(@PathVariable Long id, @RequestBody PopupDTO.Request request) {
        adminPopupService.updatePopup(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "팝업 삭제")
    public ApiResponse<Void> deletePopup(@PathVariable Long id) {
        adminPopupService.deletePopup(id);
        return ApiResponse.success(null);
    }
}

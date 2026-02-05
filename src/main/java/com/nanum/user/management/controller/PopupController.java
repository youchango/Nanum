package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.popup.dto.PopupDTO;
import com.nanum.user.management.service.PopupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @GetMapping
    @Operation(summary = "팝업 목록 조회")
    public ApiResponse<List<PopupDTO.Response>> getPopups() {
        return ApiResponse.success(popupService.getPopups());
    }
}

package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.popup.dto.PopupDTO;
import com.nanum.user.management.service.PopupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Popup", description = "Popup API")
@RestController
@RequestMapping("/api/v1/popups")
@RequiredArgsConstructor
public class PopupController implements ResponseSupport {

    private final PopupService popupService;

    @GetMapping
    @Operation(summary = "팝업 목록 조회", description = "현재 활성화된 모든 팝업 광고 목록을 조회하여 반환합니다.")
    public ResponseEntity<ApiResponse<List<PopupDTO.Response>>> getPopups() {
        return success(popupService.getPopups());
    }
}

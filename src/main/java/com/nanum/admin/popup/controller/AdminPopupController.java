package com.nanum.admin.popup.controller;

import com.nanum.admin.popup.service.AdminPopupService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.global.security.CustomUserDetails;
import com.nanum.user.popup.model.Popup;
import com.nanum.user.popup.model.PopupDTO;

import io.swagger.v3.oas.annotations.Operation;
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
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Admin Popup", description = "관리자 팝업 관리 API")
@RestController
@RequestMapping("/api/v1/admin/popups")
@RequiredArgsConstructor
public class AdminPopupController {

    private final AdminPopupService adminPopupService;

    @Operation(summary = "팝업 목록 조회", description = "팝업 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(@ModelAttribute SearchDTO searchDTO) {
        if (searchDTO.getPage() == 0)
            searchDTO.setPage(1);

        List<Popup> popupList = adminPopupService.getPopupList(searchDTO);
        int totalCount = adminPopupService.getPopupCount(searchDTO);

        // Entity -> DTO 변환
        List<PopupDTO> popupDTOList = popupList.stream()
                .map(Popup::toDTO)
                .collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("popupList", popupDTOList);
        responseData.put("totalCount", totalCount);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @Operation(summary = "팝업 상세 조회", description = "팝업 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupDTO>> detail(@PathVariable int id) {
        Popup popup = adminPopupService.getPopupDetail(id);
        if (popup == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(popup.toDTO()));
    }

    @Operation(summary = "팝업 등록", description = "새로운 팝업을 등록합니다. (Multipart Request)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> create(@ModelAttribute PopupDTO popupDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String createdBy = userDetails.getMember().getMemberCode();
            adminPopupService.registerPopup(popupDTO, createdBy);
            return ResponseEntity.ok(ApiResponse.success("팝업이 등록되었습니다.", null));
        } catch (IOException e) {
            log.error("팝업 등록 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "팝업 수정", description = "팝업 정보를 수정합니다. (Multipart Request)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable int id,
            @ModelAttribute PopupDTO popupDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String updatedBy = userDetails.getMember().getMemberCode();
            popupDTO.setPopupId(id);
            adminPopupService.updatePopup(popupDTO, updatedBy);
            return ResponseEntity.ok(ApiResponse.success("팝업이 수정되었습니다.", null));
        } catch (IOException e) {
            log.error("팝업 수정 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "팝업 삭제", description = "팝업을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable int id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String deletedBy = userDetails.getMember().getMemberCode();
        adminPopupService.deletePopup(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("팝업이 삭제되었습니다.", null));
    }
}

package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminPopupService;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.popup.dto.PopupDTO;
import com.nanum.domain.popup.dto.PopupSearchDTO;
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

    /**
     * 팝업 목록 조회 (검색 및 페이지네이션 지원).
     * @param searchDTO 검색 조건 (siteCd, searchUseYn, searchStartDate, searchEndDate, searchType, searchKeyword, page, size)
     * @return 검색된 팝업 목록
     */
    @GetMapping
    @Operation(summary = "팝업 목록 조회", description = "검색 조건(기간, 사용여부, 키워드, 사이트코드)으로 팝업 목록을 조회합니다.")
    public ApiResponse<List<PopupDTO.Response>> getPopups(@ModelAttribute PopupSearchDTO searchDTO) {
        return ApiResponse.success(adminPopupService.getPopups(searchDTO));
    }

    /**
     * 팝업 등록.
     * 프론트에서 FormData 형식으로 전송하므로 @ModelAttribute로 바인딩.
     * @param request 팝업 등록 요청 DTO
     * @param files   이미지 파일 목록 (선택)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업 등록", description = "팝업 정보(PopupDTO.Request)와 이미지 파일들을 FormData 형식으로 전달받아 신규 팝업을 등록합니다.")
    public ApiResponse<Long> createPopup(
            @ModelAttribute PopupDTO.Request request,
            @RequestParam(value = "imageFile", required = false) List<MultipartFile> files) {
        return ApiResponse.success(adminPopupService.createPopup(request, files));
    }

    /**
     * 팝업 수정.
     * @param id      팝업 ID
     * @param request 팝업 수정 요청 DTO
     * @param files   이미지 파일 목록 (선택)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업 수정", description = "특정 팝업 ID를 식별자로 하여 FormData 형식으로 팝업 정보를 업데이트합니다.")
    public ApiResponse<Void> updatePopup(
            @PathVariable Long id,
            @ModelAttribute PopupDTO.Request request,
            @RequestParam(value = "imageFile", required = false) List<MultipartFile> files) {
        adminPopupService.updatePopup(id, request, files);
        return ApiResponse.success(null);
    }

    /**
     * 팝업 삭제 (논리 삭제).
     * @param id 팝업 ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "팝업 삭제", description = "특정 팝업 ID를 식별자로 하여 논리적으로 삭제 처리합니다.")
    public ApiResponse<Void> deletePopup(@PathVariable Long id) {
        String memberCode = ManagerType.ADMIN.name();
        adminPopupService.deletePopup(id, memberCode);
        return ApiResponse.success(null);
    }
}

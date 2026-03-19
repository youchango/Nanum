package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminInquiryService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.inquiry.dto.InquiryDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminInquiry", description = "AdminInquiry API")
@RestController
@RequestMapping("/api/v1/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    @GetMapping
    @Operation(summary = "문의 목록 조회", description = "검색 조건(InquiryDTO.Search)과 페이징 정보를 기반으로 1:1 문의 목록을 조회하고 결과를 반환합니다.")
    public ApiResponse<Page<InquiryDTO.Response>> getInquiries(InquiryDTO.Search search, 
            @org.springframework.data.web.PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(adminInquiryService.getInquiries(search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "문의 상세 조회", description = "문의 ID를 식별자로 하여 특정 1:1 문의의 상세 내용 및 관리자 답변 정보를 조회합니다.")
    public ApiResponse<InquiryDTO.Response> getInquiry(@PathVariable Long id) {
        return ApiResponse.success(adminInquiryService.getInquiry(id));
    }

    @PostMapping("/{id}/reply")
    @Operation(summary = "문의 답변 등록", description = "특정 문의 ID에 대해 관리자의 답변 내용(ReplyRequest)을 등록하고 상태를 답변 완료로 변경합니다.")
    public ApiResponse<Void> replyInquiry(@PathVariable Long id, @RequestBody InquiryDTO.ReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        adminInquiryService.replyInquiry(id, request, userDetails.getUsername());
        return ApiResponse.success(null);
    }
}

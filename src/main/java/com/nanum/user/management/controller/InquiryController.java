package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.user.management.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inquiry", description = "Inquiry API")
@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController implements ResponseSupport {

    private final InquiryService inquiryService;

    @GetMapping
    @Operation(summary = "내 문의 목록 조회", description = "현재 로그인한 사용자가 작성한 모든 1:1 문의 목록을 조회하여 반환합니다.")
    public ResponseEntity<ApiResponse<List<InquiryDTO.Response>>> getMyInquiries(
            @AuthenticationPrincipal UserDetails userDetails) {
        return success(inquiryService.getMyInquiries(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "문의 상세 조회", description = "문의 ID를 식별자로 하여 특정 1:1 문의의 상세 내용 및 관리자의 답변 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<InquiryDTO.Response>> getInquiry(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return success(inquiryService.getInquiry(id, userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "문의 등록", description = "새로운 1:1 문의 내용(InquiryDTO.CreateRequest)을 작성하여 등록합니다.")
    public ResponseEntity<ApiResponse<Long>> createInquiry(@RequestBody InquiryDTO.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return success(inquiryService.createInquiry(request, userDetails.getUsername()));
    }
}

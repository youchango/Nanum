package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.user.management.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inquiry", description = "Inquiry API")
@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController implements ResponseSupport {

    private final InquiryService inquiryService;

    @GetMapping
    @Operation(summary = "내 문의 목록 조회", description = "현재 로그인한 사용자가 작성한 1:1 문의 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<InquiryDTO.Response>>> getMyInquiries(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return success(inquiryService.getMyInquiries(userDetails.getUsername(), pageable));
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

    @GetMapping("/product/{productId}")
    @Operation(summary = "상품 문의 목록", description = "특정 상품에 대한 문의 목록을 조회합니다. 비밀글은 작성자만 내용을 볼 수 있습니다.")
    public ResponseEntity<ApiResponse<Page<InquiryDTO.Response>>> getProductInquiries(
            @PathVariable Long productId,
            @RequestParam(required = false) String siteCd,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        String memberId = userDetails != null ? userDetails.getUsername() : null;
        return success(inquiryService.getProductInquiries(productId, siteCd, memberId, pageable));
    }
}

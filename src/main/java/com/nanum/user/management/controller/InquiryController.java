package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.user.management.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inquiry", description = "Inquiry API")
@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    @Operation(summary = "내 문의 목록 조회")
    public ApiResponse<List<InquiryDTO.Response>> getMyInquiries(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(inquiryService.getMyInquiries(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "문의 상세 조회")
    public ApiResponse<InquiryDTO.Response> getInquiry(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(inquiryService.getInquiry(id, userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "문의 등록")
    public ApiResponse<Long> createInquiry(@RequestBody InquiryDTO.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(inquiryService.createInquiry(request, userDetails.getUsername()));
    }
}

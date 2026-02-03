package com.nanum.admin.inquiry.controller;

import com.nanum.admin.code.service.CodeService;
import com.nanum.admin.inquiry.service.AdminInquiryService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.global.security.CustomUserDetails;
import com.nanum.user.code.model.Code;
import com.nanum.user.inquiry.model.Inquiry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin Inquiry", description = "관리자 문의 관리 API")
@RestController
@RequestMapping("/api/v1/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;
    private final CodeService codeService;

    @Operation(summary = "문의 목록 조회", description = "검색 조건에 맞는 문의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(@ModelAttribute SearchDTO searchDTO) {
        // 기본값 설정
        if (searchDTO.getPage() == 0)
            searchDTO.setPage(1);
        if (searchDTO.getPageSize() == 0)
            searchDTO.setPageSize(10);

        List<Inquiry> inquiryList = adminInquiryService.getInquiryList(searchDTO);
        int totalCount = adminInquiryService.getInquiryCount(searchDTO);

        // 문의 유형 코드 조회 (검색 필터용) - INQUIRY_TYPE
        List<Code> inquiryTypeCodes = codeService.getCodesByType("INQUIRY_TYPE");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("inquiryList", inquiryList);
        responseData.put("totalCount", totalCount);
        responseData.put("inquiryTypeCodes", inquiryTypeCodes);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @Operation(summary = "문의 상세 조회", description = "특정 문의의 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Inquiry>> detail(@PathVariable int id) {
        Inquiry inquiry = adminInquiryService.getInquiryDetail(id);

        if (inquiry == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(inquiry));
    }

    @Data
    public static class InquiryAnswerRequest {
        private String answer;
    }

    @Operation(summary = "답변 등록", description = "문의에 대한 답변을 등록합니다.")
    @PostMapping("/{id}/answer")
    public ResponseEntity<ApiResponse<String>> registerAnswer(@PathVariable int id,
            @RequestBody InquiryAnswerRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String answer = request.getAnswer();
        try {
            String answererId = userDetails.getMember().getMemberCode();
            adminInquiryService.registerAnswer(id, answer, answererId);
            return ResponseEntity.ok(ApiResponse.success("답변이 등록되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("답변 등록 중 오류가 발생했습니다."));
        }
    }
}

package com.nanum.admin.member.controller;

import com.nanum.admin.member.service.AdminMemberService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.member.model.Member;
import com.nanum.user.member.model.MemberDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자용 회원 관리 REST 컨트롤러입니다.
 */
@Tag(name = "Admin Member", description = "관리자 회원 관리 API")
@RestController
@RequestMapping("/api/v1/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    /**
     * 회원 목록을 조회합니다.
     *
     * @param searchDTO 검색 및 페이징 파라미터
     * @return 회원 목록 및 총 개수
     */
    @Operation(summary = "회원 목록 조회", description = "검색 조건에 맞는 회원 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMemberList(@ModelAttribute SearchDTO searchDTO) {
        List<Member> memberList = adminMemberService.getMemberList(searchDTO);
        int totalCount = adminMemberService.getMemberCount(searchDTO);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("memberList", memberList);
        responseData.put("totalCount", totalCount);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    /**
     * 회원을 생성합니다. (Master, Biz, User 모두 가능)
     *
     * @param memberDTO     회원 가입 정보
     * @param bindingResult 유효성 검증 결과
     * @return 성공 시 생성된 회원 정보 (또는 성공 메시지)
     */
    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다. 권한(role)에 따라 타입이 결정됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createMember(@Valid @RequestBody MemberDTO memberDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Validation error";
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
        }

        try {
            adminMemberService.createMember(memberDTO);
            return ResponseEntity.ok(ApiResponse.success("Member created successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 회원 상세 정보를 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 회원 상세 정보
     */
    @Operation(summary = "회원 상세 조회", description = "회원 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{memberCode}")
    public ResponseEntity<ApiResponse<Member>> getMember(@PathVariable String memberCode) {
        try {
            Member member = adminMemberService.getMember(memberCode);
            return ResponseEntity.ok(ApiResponse.success(member));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 회원 정보를 수정합니다.
     *
     * @param memberId  회원 ID
     * @param memberDTO 수정할 정보
     * @return 성공 메시지
     */
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PutMapping("/{memberCode}")
    public ResponseEntity<ApiResponse<Object>> updateMember(@PathVariable String memberCode,
            @RequestBody MemberDTO memberDTO) {
        try {
            adminMemberService.updateMember(memberCode, memberDTO);
            return ResponseEntity.ok(ApiResponse.success("Member updated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

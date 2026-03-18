package com.nanum.user.member.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.member.dto.PasswordResetRequest;
import com.nanum.domain.member.dto.PasswordResetResponse;
import com.nanum.domain.member.dto.ProfileResponse;
import com.nanum.domain.member.dto.ProfileUpdateRequest;
import com.nanum.domain.member.model.MemberType;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Operation(summary = "아이디 중복 확인", description = "회원 유형별로 아이디 중복 여부를 확인합니다. (true: 중복됨, false: 사용 가능)")
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkIdDuplicate(
            @RequestParam String memberId,
            @RequestParam MemberType memberType) {
        boolean exists = memberRepository.existsByMemberIdAndMemberType(memberId, memberType);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 회원의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("회원 정보 조회 성공", memberService.getProfile(principal.getName())));
    }

    @Operation(summary = "내 프로필 수정", description = "현재 로그인한 회원의 프로필 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Principal principal) {
        memberService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다.", null));
    }

    @Operation(summary = "비밀번호 찾기", description = "회원 정보 확인 후 임시 비밀번호를 발급합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(ApiResponse.success("임시 비밀번호가 발급되었습니다.", memberService.resetPassword(request)));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 회원의 탈퇴를 처리합니다. 비밀번호 확인 필수입니다.")
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @RequestBody java.util.Map<String, String> body,
            Principal principal) {
        String password = body.get("password");
        memberService.withdraw(principal.getName(), password);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
    }
}

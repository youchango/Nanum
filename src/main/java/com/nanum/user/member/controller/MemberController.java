package com.nanum.user.member.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.member.dto.PasswordResetRequest;
import com.nanum.domain.member.dto.PasswordResetResponse;
import com.nanum.domain.member.dto.ProfileResponse;
import com.nanum.domain.member.dto.ProfileUpdateRequest;
import com.nanum.domain.member.model.MemberType;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.member.service.MemberService;
import com.nanum.user.member.service.SmsVerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final SmsVerifyService smsVerifyService;

    @Value("${sms.dev-mode:true}")
    private boolean smsDevMode;

    @Operation(summary = "아이디 중복 확인")
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkIdDuplicate(
            @RequestParam String memberId,
            @RequestParam MemberType memberType) {
        boolean exists = memberRepository.existsByMemberIdAndMemberType(memberId, memberType);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @Operation(summary = "SMS 인증번호 발송", description = "회원정보 확인 후 휴대전화번호로 6자리 인증번호를 발송합니다.")
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendCode(@RequestBody Map<String, String> body) {
        String memberId = body.get("memberId");
        String memberName = body.get("memberName");
        String mobilePhone = body.get("mobilePhone");
        String purpose = body.getOrDefault("purpose", "RESET_PASSWORD");

        if (mobilePhone == null || mobilePhone.isBlank()) {
            return ResponseEntity.ok(ApiResponse.error("휴대전화번호를 입력해주세요."));
        }

        // 비밀번호 재설정 목적일 경우 회원정보 사전 검증
        if ("RESET_PASSWORD".equals(purpose)) {
            if (memberId == null || memberId.isBlank() || memberName == null || memberName.isBlank()) {
                return ResponseEntity.ok(ApiResponse.error("아이디와 이름을 입력해주세요."));
            }
            var member = memberRepository.findByMemberId(memberId).orElse(null);
            if (member == null) {
                return ResponseEntity.ok(ApiResponse.error("입력하신 정보와 일치하는 회원을 찾을 수 없습니다."));
            }
            if (!member.getMemberName().equals(memberName) || !member.getMobilePhone().equals(mobilePhone)) {
                return ResponseEntity.ok(ApiResponse.error("입력하신 정보와 일치하는 회원을 찾을 수 없습니다."));
            }
            if ("Y".equals(member.getWithdrawYn())) {
                return ResponseEntity.ok(ApiResponse.error("탈퇴한 회원입니다."));
            }
        }

        String code = smsVerifyService.sendCode(mobilePhone, purpose);

        if (smsDevMode) {
            return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다. (개발모드)",
                    Map.of("devCode", code)));
        }
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다.", null));
    }

    @Operation(summary = "SMS 인증번호 확인", description = "발송된 인증번호를 검증합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestBody Map<String, String> body) {
        String mobilePhone = body.get("mobilePhone");
        String purpose = body.getOrDefault("purpose", "RESET_PASSWORD");
        String code = body.get("code");

        if (mobilePhone == null || code == null) {
            return ResponseEntity.ok(ApiResponse.error("휴대전화번호와 인증번호를 입력해주세요."));
        }

        boolean result = smsVerifyService.verifyCode(mobilePhone, purpose, code);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("인증이 완료되었습니다.", true));
        }
        return ResponseEntity.ok(ApiResponse.error("인증번호가 일치하지 않거나 만료되었습니다."));
    }

    @Operation(summary = "비밀번호 찾기", description = "SMS 인증 완료 후 임시 비밀번호를 발급합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        try {
            // SMS 인증 완료 여부 확인
            if (!smsVerifyService.isVerified(request.getMobilePhone(), "RESET_PASSWORD")) {
                return ResponseEntity.ok(ApiResponse.error("휴대전화 인증을 먼저 완료해주세요."));
            }
            return ResponseEntity.ok(ApiResponse.success("임시 비밀번호가 발급되었습니다.",
                    memberService.resetPassword(request)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("회원 정보 조회 성공",
                memberService.getProfile(principal.getName())));
    }

    @Operation(summary = "내 프로필 수정")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request, Principal principal) {
        memberService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다.", null));
    }

    @Operation(summary = "회원 탈퇴")
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @RequestBody Map<String, String> body, Principal principal) {
        memberService.withdraw(principal.getName(), body.get("password"));
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
    }
}

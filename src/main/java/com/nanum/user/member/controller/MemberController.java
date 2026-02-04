package com.nanum.user.member.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.member.model.MemberType;
import com.nanum.user.member.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @Operation(summary = "아이디 중복 확인", description = "회원 유형별로 아이디 중복 여부를 확인합니다. (true: 중복됨, false: 사용 가능)")
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkIdDuplicate(
            @RequestParam String memberId,
            @RequestParam MemberType memberType) {
        boolean exists = memberRepository.existsByMemberIdAndMemberType(memberId, memberType);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}

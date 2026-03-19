package com.nanum.user.claim.controller;

import com.nanum.domain.claim.dto.ClaimDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.user.claim.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Claim", description = "교환/반품/환불 API")
@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@org.springframework.stereotype.Component("userClaimController")
public class ClaimController implements ResponseSupport {

    private final ClaimService claimService;

    @Operation(summary = "클레임 접수", description = "교환/반품/환불을 접수합니다. 배송 완료된 주문에 대해서만 가능합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createClaim(
            @Valid @RequestBody ClaimDTO.CreateRequest request,
            Principal principal) {
        Long claimId = claimService.createClaim(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("클레임이 접수되었습니다.", claimId));
    }

    @Operation(summary = "내 클레임 목록 조회", description = "현재 로그인한 사용자의 클레임 내역을 최신순으로 페이징 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClaimDTO.Response>>> getMyClaims(
            Principal principal,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("클레임 목록 조회 성공",
                claimService.getMyClaims(principal.getName(), pageable)));
    }

    @Operation(summary = "클레임 상세 조회", description = "특정 클레임의 상세 정보를 조회합니다. 본인 클레임만 조회 가능합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimDTO.Response>> getClaimDetail(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("클레임 상세 조회 성공",
                claimService.getClaimDetail(principal.getName(), id)));
    }
}

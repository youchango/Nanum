package com.nanum.admin.claim.controller;

import com.nanum.admin.claim.entity.Claim;
import com.nanum.admin.claim.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Claim", description = "Claim API")
@RestController
@RequestMapping("/api/v1/admin/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @Operation(summary = "전체 클레임 목록 조회", description = "데이터베이스의 클레임(취소/교환/반품) 테이블 전체 목록을 단일 요청으로 조회하고, 리스트 형태로 반환합니다.")
    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @Operation(summary = "단일 클레임 상세 조회", description = "요청받은 클레임 식별자(id)를 기준으로 데이터베이스를 조회하고, 상세 정보를 반환합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaim(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.getClaim(id));
    }

    @Operation(summary = "클레임 상태 수정", description = "입력받은 클레임 ID, 변경할 상태값(status), 담당 관리자 코드(managerCode)를 기준으로 클레임의 처리 상태를 변경하고 결과를 반환합니다.")
    @PutMapping("/{id}/status")
    public ResponseEntity<Claim> updateClaimStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String managerCode) {
        return ResponseEntity.ok(claimService.updateClaimStatus(id, status, managerCode));
    }
}

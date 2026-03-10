package com.nanum.admin.site.controller;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.admin.site.dto.SitePolicyDTO;
import com.nanum.admin.site.service.SitePolicyService;
import com.nanum.domain.shop.model.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "SitePolicy", description = "SitePolicy API")
@RestController
@RequestMapping("/api/v1/admin/sites")
@RequiredArgsConstructor
public class SitePolicyController {

    private final SitePolicyService sitePolicyService;

    // [MASTER] 전체 사이트 목록 조회
    @Operation(summary = "전체 사이트 목록 조회", description = "[MASTER] 시스템에 등록된 모든 사이트(ShopInfo) 목록을 조회합니다. MASTER 권한이 필요합니다.")
    @GetMapping("/")
    public ResponseEntity<List<ShopInfo>> getAllSites(@AuthenticationPrincipal CustomManagerDetails details) {
        if (!"MASTER".equals(details.getManager().getMbType())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(sitePolicyService.getAllSites());
    }

    // [ADMIN] 내 사이트 정책 조회
    @Operation(summary = "내 사이트 정책 조회", description = "[ADMIN] 현재 로그인된 관리자의 소속 사이트에 설정된 정책 가이드 정보를 조회합니다.")
    @GetMapping("/policy")
    public ResponseEntity<SitePolicyDTO> getMyPolicy(@AuthenticationPrincipal CustomManagerDetails details) {
        Manager manager = details.getManager();
        if (manager.getSiteCd() == null || manager.getSiteCd().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(sitePolicyService.getPolicyBySiteCd(manager.getSiteCd()));
    }

    // [MASTER/ADMIN] 특정 사이트 정책 조회
    @Operation(summary = "특정 사이트 정책 조회", description = "[MASTER/ADMIN] 특정 사이트 코드(siteCd)를 기반으로 해당 사이트의 정책 정보를 조회합니다. MASTER는 모든 사이트, ADMIN은 본인 사이트만 가능합니다.")
    @GetMapping("/policy/{siteCd}")
    public ResponseEntity<SitePolicyDTO> getPolicy(
            @PathVariable("siteCd") String siteCd,
            @AuthenticationPrincipal CustomManagerDetails details) {
        Manager manager = details.getManager();

        // 권한 체크: MASTER는 통과, ADMIN은 본인 사이트만 가능
        if (!"MASTER".equals(manager.getMbType())) {
            if (!siteCd.equals(manager.getSiteCd())) {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(sitePolicyService.getPolicyBySiteCd(siteCd));
    }

    // [MASTER/ADMIN] 정책 수정
    @Operation(summary = "정책 정보 수정", description = "[MASTER/ADMIN] 특정 사이트의 정책 정보를 요청받은 데이터로 업데이트합니다. MASTER는 모든 사이트, ADMIN은 본인 사이트만 수정 가능합니다.")
    @PutMapping("/policy/{siteCd}")
    public ResponseEntity<Void> updatePolicy(
            @PathVariable("siteCd") String siteCd,
            @RequestBody SitePolicyDTO dto,
            @AuthenticationPrincipal CustomManagerDetails details) {
        Manager manager = details.getManager();

        // 권한 체크: MASTER는 통과, ADMIN은 본인 사이트만 가능
        if (!"MASTER".equals(manager.getMbType())) {
            if (!siteCd.equals(manager.getSiteCd())) {
                return ResponseEntity.status(403).build();
            }
        }

        sitePolicyService.savePolicy(siteCd, dto, manager);
        return ResponseEntity.ok().build();
    }
}

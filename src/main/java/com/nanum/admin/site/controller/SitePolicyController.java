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

@Tag(name = "SitePolicy", description = "SitePolicy API")
@RestController
@RequestMapping("/api/v1/admin/sites")
@RequiredArgsConstructor
public class SitePolicyController {

    private final SitePolicyService sitePolicyService;

    // [MASTER] 전체 사이트 목록 조회
    @GetMapping("/")
    public ResponseEntity<List<ShopInfo>> getAllSites(@AuthenticationPrincipal CustomManagerDetails details) {
        if (!"MASTER".equals(details.getManager().getMbType())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(sitePolicyService.getAllSites());
    }

    // [ADMIN] 내 사이트 정책 조회
    @GetMapping("/policy")
    public ResponseEntity<SitePolicyDTO> getMyPolicy(@AuthenticationPrincipal CustomManagerDetails details) {
        Manager manager = details.getManager();
        if (manager.getSiteCd() == null || manager.getSiteCd().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(sitePolicyService.getPolicyBySiteCd(manager.getSiteCd()));
    }

    // [MASTER/ADMIN] 특정 사이트 정책 조회
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

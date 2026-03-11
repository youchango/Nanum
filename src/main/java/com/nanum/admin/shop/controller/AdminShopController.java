package com.nanum.admin.shop.controller;

import com.nanum.admin.shop.service.AdminShopService;
import com.nanum.domain.shop.dto.ShopDTO;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "AdminShop", description = "AdminShop API")
@RestController
@RequestMapping("/api/v1/admin/shops")
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    @Operation(summary = "전체 상점 목록 조회", description = "데이터베이스에 등록된 모든 상점(공급사/사이트)의 목록을 조회하여 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopDTO.Response>>> getAllShops() {
        return ResponseEntity.ok(ApiResponse.success(adminShopService.getAllShops()));
    }

    @Operation(summary = "단일 상점 상세 조회", description = "상점 고유 키(shopKey)를 기반으로 특정 상점의 상세 설정 정보를 조회합니다.")
    @GetMapping("/{shopKey}")
    public ResponseEntity<ApiResponse<ShopDTO.Response>> getShop(@PathVariable Long shopKey) {
        return ResponseEntity.ok(ApiResponse.success(adminShopService.getShop(shopKey)));
    }

    @Operation(summary = "상점 신규 등록", description = "새로운 상점 가입 정보(ShopDTO.Request)를 전달받아 시스템에 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createShop(@RequestBody ShopDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adminShopService.createShop(request)));
    }

    @Operation(summary = "상점 정보 수정", description = "기존 상점의 설정 및 기본 정보를 요청받은 데이터로 업데이트합니다.")
    @PutMapping("/{shopKey}")
    public ResponseEntity<ApiResponse<String>> updateShop(@PathVariable Long shopKey,
            @RequestBody ShopDTO.Request request) {
        adminShopService.updateShop(shopKey, request);
        return ResponseEntity.ok(ApiResponse.success("상점 정보가 수정되었습니다."));
    }

    @Operation(summary = "상점 삭제", description = "상점 고유 키를 기반으로 해당 상점 정보를 삭제(비활성화) 처리합니다.")
    @DeleteMapping("/{shopKey}")
    public ResponseEntity<ApiResponse<String>> deleteShop(@PathVariable Long shopKey) {
        adminShopService.deleteShop(shopKey);
        return ResponseEntity.ok(ApiResponse.success("상점이 삭제되었습니다."));
    }
}

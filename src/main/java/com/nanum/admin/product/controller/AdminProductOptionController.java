package com.nanum.admin.product.controller;

import com.nanum.admin.product.service.AdminProductOptionService;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "AdminProductOption", description = "AdminProductOption API")
@RestController
@RequestMapping("/admin/api/products/{productId}/options")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MASTER', 'SCM')")
public class AdminProductOptionController {

    private final AdminProductOptionService adminProductOptionService;

    @Operation(summary = "상품 옵션 생성", description = "특정 상품 ID에 대해 새로운 하위 옵션 정보(ProductDTO.Option)를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOption(
            @PathVariable Long productId,
            @RequestBody ProductDTO.Option request) {
        adminProductOptionService.createOption(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "상품 옵션 수정", description = "특정 상품의 기존 옵션 정보를 요청받은 데이터로 업데이트합니다.")
    @PutMapping("/{optionId}")
    public ResponseEntity<ApiResponse<Void>> updateOption(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @RequestBody ProductDTO.Option request) {
        adminProductOptionService.updateOption(productId, optionId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "상품 옵션 삭제", description = "상품 ID와 옵션 ID를 식별자로 하여 해당 옵션을 삭제 처리합니다.")
    @DeleteMapping("/{optionId}")
    public ResponseEntity<ApiResponse<Void>> deleteOption(
            @PathVariable Long productId,
            @PathVariable Long optionId) {
        adminProductOptionService.deleteOption(productId, optionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

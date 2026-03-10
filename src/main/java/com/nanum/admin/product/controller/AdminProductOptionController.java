package com.nanum.admin.product.controller;

import com.nanum.admin.product.service.AdminProductOptionService;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminProductOption", description = "AdminProductOption API")
@RestController
@RequestMapping("/admin/api/products/{productId}/options")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MASTER', 'SCM')")
public class AdminProductOptionController {

    private final AdminProductOptionService adminProductOptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOption(
            @PathVariable Long productId,
            @RequestBody ProductDTO.Option request) {
        adminProductOptionService.createOption(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<ApiResponse<Void>> updateOption(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @RequestBody ProductDTO.Option request) {
        adminProductOptionService.updateOption(productId, optionId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<ApiResponse<Void>> deleteOption(
            @PathVariable Long productId,
            @PathVariable Long optionId) {
        adminProductOptionService.deleteOption(productId, optionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

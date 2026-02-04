package com.nanum.admin.product.controller;

import com.nanum.admin.product.service.AdminProductCategoryService;
import com.nanum.user.product.model.ProductCategory;
import com.nanum.product.model.ProductCategoryDTO;
import com.nanum.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Product Category", description = "관리자 상품 카테고리 관리 API")
@RestController
@RequestMapping("/api/v1/admin/category")
@RequiredArgsConstructor
public class AdminProductCategoryController {

    private final AdminProductCategoryService adminProductCategoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 상품 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategory>> createCategory(@RequestBody ProductCategoryDTO dto) {
        ProductCategory created = adminProductCategoryService.createCategory(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @Operation(summary = "카테고리 수정", description = "기존 상품 카테고리 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategory>> updateCategory(
            @PathVariable Long id,
            @RequestBody ProductCategoryDTO dto) {
        ProductCategory updated = adminProductCategoryService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "카테고리 삭제", description = "상품 카테고리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        adminProductCategoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "카테고리 사용 여부 변경", description = "카테고리의 사용 여부(Y/N)를 변경합니다.")
    @PatchMapping("/{id}/use-yn")
    public ResponseEntity<ApiResponse<Void>> toggleUseYn(
            @PathVariable Long id,
            @RequestParam String useYn) {
        adminProductCategoryService.toggleUseYn(id, useYn);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "전체 카테고리 목록 조회", description = "관리자용 전체 카테고리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductCategoryDTO>>> getAllCategories() {
        List<ProductCategoryDTO> list = adminProductCategoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}

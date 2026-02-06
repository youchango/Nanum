package com.nanum.admin.product.controller;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.nanum.admin.product.service.AdminProductService;
import com.nanum.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.ProductStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin Product", description = "관리자 상품 관리 API")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "검색 조건에 따른 상품 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AdminProductListDTO>>> getProducts(
            @ModelAttribute AdminProductSearchDTO searchDTO,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminProductService.getProducts(searchDTO, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ProductDTO.Response>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminProductService.getProduct(id)));
    }

    @PostMapping
    @Operation(summary = "상품 등록", description = "신규 상품을 등록합니다.")
    public ResponseEntity<ApiResponse<Long>> createProduct(@RequestBody ProductDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.success(adminProductService.createProduct(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateProduct(@PathVariable Long id,
            @RequestBody ProductDTO.Request request) {
        adminProductService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "상품 상태 변경", description = "상품의 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @PathVariable Long id,
            @RequestBody Map<String, ProductStatus> statusMap) {
        adminProductService.updateProductStatus(id, statusMap.get("status"));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        // TODO: Get actual logged-in member code
        String memberCode = "ADMIN";
        adminProductService.deleteProduct(id, memberCode);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

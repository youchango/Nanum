package com.nanum.admin.product.controller;

import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.nanum.admin.product.service.AdminProductService;
import com.nanum.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.dto.ProductSitePriceUpdateDTO;
import com.nanum.admin.product.service.AdminProductReviewService;
import com.nanum.domain.product.dto.AdminProductReviewDTO;
import com.nanum.domain.product.dto.AdminProductReviewSearchDTO;
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
    private final AdminProductReviewService adminProductReviewService;

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "검색 조건에 따른 상품 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProducts(
            @ModelAttribute AdminProductSearchDTO searchDTO) {
        return ResponseEntity.ok(ApiResponse.success(adminProductService.getProducts(searchDTO)));
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

    @PutMapping("/{id}/sites/{siteCd}/price")
    @Operation(summary = "사이트별 상품 및 옵션 가격 일괄 업데이트", description = "특정 사이트에 대한 상품 기본 가격, 노출 여부, 옵션별 추가금액을 일괄 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateProductSitePrice(
            @PathVariable Long id,
            @PathVariable String siteCd,
            @RequestBody ProductSitePriceUpdateDTO request) {
        adminProductService.updateProductSitePrice(id, siteCd, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "상품 사이트별 삭제", description = "상품을 선택한 사이트에서만 제외(논리 삭제)합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id, @RequestParam String siteCd) {
        adminProductService.deleteProduct(id, siteCd);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}/master")
    @Operation(summary = "상품 원본(마스터) 완전 삭제", description = "상품 원본을 모든 쇼핑몰 환경에서 영구적으로 제외합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMasterProduct(@PathVariable Long id) {
        adminProductService.deleteMasterProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // --- Product Review Monitoring ---
    @GetMapping("/reviews")
    @Operation(summary = "전체 상품 리뷰 목록 조회", description = "상품 리뷰 목록을 모니터링합니다.")
    public ResponseEntity<ApiResponse<Page<AdminProductReviewDTO.Response>>> getReviews(
            @ModelAttribute AdminProductReviewSearchDTO searchDTO,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminProductReviewService.getReviews(searchDTO, pageable)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "부적절한 리뷰 논리 삭제", description = "관리자 권한으로 특정 리뷰를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId) {
        adminProductReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

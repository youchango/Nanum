package com.nanum.user.product.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.user.product.service.ProductService;
import com.nanum.user.product.service.ProductReviewService;
import com.nanum.domain.product.dto.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product", description = "Product API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductReviewService productReviewService;

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "상품 목록 조회", description = "상품 전체 목록을 페이징하여 조회합니다.")
    public ApiResponse<List<ProductDTO.MallProductResponse>> getProducts(
            @RequestParam String siteCd,
            com.nanum.global.common.dto.SearchDTO searchDTO) {
        String memberCode = getCurrentMemberCode();
        return ApiResponse.success(productService.getMallProductList(searchDTO, siteCd, memberCode));
    }

    @GetMapping("/main")
    @io.swagger.v3.oas.annotations.Operation(summary = "메인 페이지 상품 목록 조회", description = "메인 페이지용 상품 목록을 조회수 내림차순으로 페이징하여 조회합니다.")
    public ApiResponse<List<ProductDTO.MallProductResponse>> getMainProducts(
            @RequestParam String siteCd,
            com.nanum.global.common.dto.SearchDTO searchDTO) {
        String memberCode = getCurrentMemberCode();
        return ApiResponse.success(productService.getMainProductList(searchDTO, siteCd, memberCode));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO.MallProductResponse> getProduct(
            @PathVariable Long id,
            @RequestParam String siteCd) {
        String memberCode = getCurrentMemberCode();
        return ApiResponse.success(productService.getMallProduct(id, siteCd, memberCode));
    }

    // --- Product Review & Like ---
    @GetMapping("/{productId}/reviews")
    public ApiResponse<Page<ProductReviewDTO.Response>> getReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {
        String currentMemberCode = getCurrentMemberCode();
        return ApiResponse.success(productReviewService.getReviews(productId, currentMemberCode, pageable));
    }

    @PostMapping("/{productId}/reviews")
    public ApiResponse<Long> createReview(
            @PathVariable Long productId,
            @RequestBody ProductReviewDTO.Request request) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        return ApiResponse.success(productReviewService.createReview(productId, currentMemberCode, request));
    }

    @PutMapping("/{productId}/reviews/{reviewId}")
    public ApiResponse<Void> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestBody ProductReviewDTO.Request request) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.updateReview(reviewId, currentMemberCode, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.deleteReview(reviewId, currentMemberCode);
        return ApiResponse.success(null);
    }

    @PostMapping("/{productId}/reviews/{reviewId}/like")
    public ApiResponse<Void> addLike(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.toggleLike(reviewId, currentMemberCode, true);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}/like")
    public ApiResponse<Void> removeLike(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.toggleLike(reviewId, currentMemberCode, false);
        return ApiResponse.success(null);
    }

    // Helper
    private String getCurrentMemberCode() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // CustomUserDetails 사용 패키지에 맞추어 임시 변환
        try {
            java.lang.reflect.Method getMemberCode = principal.getClass().getMethod("getMemberCode");
            return (String) getMemberCode.invoke(principal);
        } catch (Exception e) {
            return null; // 인증되지 않은 사용자
        }
    }
}

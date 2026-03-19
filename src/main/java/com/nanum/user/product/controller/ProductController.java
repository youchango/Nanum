package com.nanum.user.product.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.user.product.service.ProductService;
import com.nanum.user.product.service.ProductReviewService;
import com.nanum.domain.product.dto.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.nanum.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Product", description = "Product API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController implements ResponseSupport {

    private final ProductService productService;
    private final ProductReviewService productReviewService;

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "사이트 코드와 검색 조건을 기반으로 전체 상품 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getProducts(
            @RequestParam String siteCd,
            @ModelAttribute com.nanum.global.common.dto.SearchDTO searchDTO) {
        String memberCode = getCurrentMemberCode();
        var result = productService.getMallProductListWithCount(searchDTO, siteCd, memberCode);
        return success(result);
    }

    @GetMapping("/main")
    @Operation(summary = "메인 상품 목록 조회", description = "메인 페이지 노출용 상품 목록을 조회수 순으로 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProductDTO.MallProductResponse>>> getMainProducts(
            @RequestParam String siteCd,
            @ModelAttribute com.nanum.global.common.dto.SearchDTO searchDTO) {
        String memberCode = getCurrentMemberCode();
        return success(productService.getMainProductList(searchDTO, siteCd, memberCode));
    }

    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID와 사이트 코드를 기반으로 특정 상품의 상세 정보 및 옵션을 조회합니다.")
    public ResponseEntity<ApiResponse<ProductDTO.MallProductResponse>> getProduct(
            @PathVariable Long id,
            @RequestParam String siteCd,
            @CookieValue(name = "viewed_products", defaultValue = "") String viewedProducts,
            jakarta.servlet.http.HttpServletResponse response) {
        String memberCode = getCurrentMemberCode();

        // 쿠키 기반 중복 조회 방지
        String productIdStr = String.valueOf(id);
        if (!viewedProducts.contains("[" + productIdStr + "]")) {
            productService.increaseViewCount(id);
            String newValue = viewedProducts + "[" + productIdStr + "]";
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("viewed_products", newValue);
            cookie.setMaxAge(24 * 60 * 60); // 24시간
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        return success(productService.getMallProduct(id, siteCd, memberCode));
    }

    // --- Product Review & Like ---
    @GetMapping("/{productId}/reviews")
    @Operation(summary = "상품 리뷰 목록 조회", description = "특정 상품에 대한 모든 리뷰 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<ProductReviewDTO.Response>>> getReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {
        String currentMemberCode = getCurrentMemberCode();
        return success(productReviewService.getReviews(productId, currentMemberCode, pageable));
    }

    @PostMapping("/{productId}/reviews")
    @Operation(summary = "상품 리뷰 등록", description = "구매한 상품에 대해 새로운 리뷰 내용(ProductReviewDTO.Request)을 작성합니다.")
    public ResponseEntity<ApiResponse<Long>> createReview(
            @PathVariable Long productId,
            @RequestBody ProductReviewDTO.Request request) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        return success(productReviewService.createReview(productId, currentMemberCode, request));
    }

    @PutMapping("/{productId}/reviews/{reviewId}")
    @Operation(summary = "상품 리뷰 수정", description = "작성한 리뷰의 내용이나 별점을 업데이트합니다.")
    public ResponseEntity<ApiResponse<Void>> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestBody ProductReviewDTO.Request request) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.updateReview(reviewId, currentMemberCode, request);
        return ok();
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}")
    @Operation(summary = "상품 리뷰 삭제", description = "작성한 특정 리뷰를 삭제 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.deleteReview(reviewId, currentMemberCode);
        return ok();
    }

    @PostMapping("/{productId}/reviews/{reviewId}/like")
    @Operation(summary = "리뷰 좋아요 추가", description = "특정 리뷰에 대해 좋아요(추천)를 추가합니다.")
    public ResponseEntity<ApiResponse<Void>> addLike(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.toggleLike(reviewId, currentMemberCode, true);
        return ok();
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}/like")
    @Operation(summary = "리뷰 좋아요 취소", description = "특정 리뷰에 추가했던 좋아요를 취소합니다.")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        String currentMemberCode = getCurrentMemberCode();
        if (currentMemberCode == null)
            throw new com.nanum.global.error.exception.BusinessException(
                    com.nanum.global.error.ErrorCode.ACCESS_DENIED);
        productReviewService.toggleLike(reviewId, currentMemberCode, false);
        return ok();
    }

    private String getCurrentMemberCode() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return ((CustomUserDetails) auth.getPrincipal()).getMember().getMemberCode();
    }
}

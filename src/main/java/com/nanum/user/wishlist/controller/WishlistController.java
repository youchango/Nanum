package com.nanum.user.wishlist.controller;

import com.nanum.domain.wishlist.dto.WishlistDTO;
import com.nanum.user.wishlist.service.WishlistService;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Wishlist", description = "Wishlist API")
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "찜 목록 토글", description = "특정 상품을 찜 목록에 추가하거나 이미 있을 경우 삭제 처리(Toggle)합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(
            @RequestBody WishlistDTO.Request request,
            Principal principal) {
        boolean isAdded = wishlistService.toggleWishlist(principal.getName(), request.getProductId());
        return ResponseEntity.ok(ApiResponse.success(isAdded));
    }

    @Operation(summary = "찜 상품 삭제", description = "특정 상품 ID를 기반으로 찜 목록에서 해당 상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteWishlist(
            @PathVariable Long productId,
            Principal principal) {
        wishlistService.deleteWishlist(principal.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "내 찜 목록 조회", description = "현재 사용자가 찜한 모든 상품 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<WishlistDTO.Response>>> getMyWishlist(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal) {
        Page<WishlistDTO.Response> wishlist = wishlistService.getMyWishlist(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }
}

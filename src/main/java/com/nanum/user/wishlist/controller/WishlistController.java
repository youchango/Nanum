package com.nanum.user.wishlist.controller;

import com.nanum.domain.product.dto.ProductDTO;
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

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(
            @RequestBody WishlistDTO.Request request,
            Principal principal) {
        boolean isAdded = wishlistService.toggleWishlist(principal.getName(), request.getProductId());
        return ResponseEntity.ok(ApiResponse.success(isAdded));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteWishlist(
            @PathVariable Long productId,
            Principal principal) {
        wishlistService.deleteWishlist(principal.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDTO.Response>>> getMyWishlist(
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal) {
        Page<ProductDTO.Response> wishlist = wishlistService.getMyWishlist(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }
}

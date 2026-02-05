package com.nanum.user.cart.controller;

import com.nanum.domain.cart.dto.CartDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.exception.DuplicateCartItemException;
import com.nanum.user.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addToCart(
            @RequestBody CartDTO.AddRequest request,
            Principal principal) {
        Long cartId = cartService.addToCart(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("장바구니에 상품이 담겼습니다.", cartId));
    }

    @ExceptionHandler(DuplicateCartItemException.class)
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> handleDuplicateCartItemException(
            DuplicateCartItemException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Map<String, Boolean>>builder()
                        .status("CONFLICT")
                        .message("이미 장바구니에 담긴 상품입니다. 수량을 추가하시겠습니까?")
                        .data(Map.of("exists", true))
                        .build());
    }
}

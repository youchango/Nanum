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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Cart", description = "Cart API")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 상품 추가", description = "현재 로그인한 사용자의 장바구니에 특정 상품(CartDTO.AddRequest)을 추가합니다. 이미 존재하는 상품일 경우 예외가 발생합니다.")
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

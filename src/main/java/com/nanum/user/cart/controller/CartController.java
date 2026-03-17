package com.nanum.user.cart.controller;

import com.nanum.domain.cart.dto.CartDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.exception.DuplicateCartItemException;
import com.nanum.user.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
            @Valid @RequestBody CartDTO.AddRequest request,
            Principal principal) {
        Long cartId = cartService.addToCart(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("장바구니에 상품이 담겼습니다.", cartId));
    }

    @Operation(summary = "장바구니 목록 조회", description = "현재 로그인한 사용자의 장바구니 리스트를 옵션 및 권한별 계산된 가격 형식으로 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<CartDTO.Response>>> getCartList(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("장바구니 목록 조회 성공",
                cartService.getCartList(principal.getName())));
    }

    @Operation(summary = "장바구니 수량 변경", description = "특정 장바구니 항목의 수량을 업데이트합니다.")
    @PutMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> updateCartQuantity(
            @PathVariable("cartId") Long cartId,
            @Valid @RequestBody CartDTO.Request request,
            Principal principal) {
        cartService.updateCartQuantity(cartId, request.getQuantity(), principal.getName());
        return ResponseEntity.ok(ApiResponse.success("수량이 변경되었습니다.", null));
    }

    @Operation(summary = "장바구니 단건 삭제", description = "특정 장바구니 항목을 삭제합니다.")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable("cartId") Long cartId,
            Principal principal) {
        cartService.deleteCartItem(cartId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다.", null));
    }

    @Operation(summary = "장바구니 선택 삭제", description = "여러 장바구니 항목을 배열로 받아 일괄 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteCartItems(
            @RequestBody java.util.List<Long> cartIds,
            Principal principal) {
        cartService.deleteCartItems(cartIds, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("선택한 상품이 삭제되었습니다.", null));
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

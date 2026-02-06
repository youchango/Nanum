package com.nanum.user.cart.service;

import com.nanum.domain.cart.dto.CartDTO;
import com.nanum.domain.cart.model.Cart;
import com.nanum.domain.cart.repository.CartRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.global.exception.DuplicateCartItemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long addToCart(String memberId, CartDTO.AddRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // Duplicate Check
        Optional<Cart> existingCart = cartRepository.findByMemberMemberCodeAndProduct_IdAndOptionId(
                member.getMemberCode(),
                request.getProductId(),
                request.getOptionId());

        if (existingCart.isPresent()) {
            if (request.isForceUpdate()) {
                // 2차 요청: 수량 합산
                Cart cart = existingCart.get();
                cart.addQuantity(request.getQuantity());
                return cart.getCartId();
            } else {
                // 1차 요청: Conflict Exception 발생
                throw new DuplicateCartItemException("이미 장바구니에 담긴 상품입니다.");
            }
        }

        // 신규 등록
        Cart newCart = Cart.builder()
                .member(member)
                .product(product)
                .optionId(request.getOptionId())
                .quantity(request.getQuantity())
                .build();

        return cartRepository.save(newCart).getCartId();
    }
}

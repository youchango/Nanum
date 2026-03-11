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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final com.nanum.domain.product.repository.ProductSiteRepository productSiteRepository;

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

    /**
     * 장바구니 리스트 조회 (Role에 따른 가격 차등 적용)
     */
    public List<CartDTO.Response> getCartList(String memberCode, com.nanum.domain.member.model.MemberRole role) {
        List<Cart> cartList = cartRepository.findCartListWithDetailsByMemberCode(memberCode);

        return cartList.stream().map(cart -> {
            CartDTO.Response response = new CartDTO.Response();
            response.setCartId(cart.getCartId());
            response.setProductId(cart.getProduct().getId());
            response.setOptionId(cart.getOptionId());
            response.setProductName(cart.getProduct().getName());
            response.setQuantity(cart.getQuantity());

            // 1. 권한별 기본 가격 측정 (ProductSite 기준)
            int calculatedBasePrice = cart.getProduct().getRetailPrice() != null ? cart.getProduct().getRetailPrice()
                    : 0;

            // ProductSite에서 해당 상품의 사이트별 가격 정보 조회 (장바구니는 통상적으로 주력 사이트 코드를 쓰거나 첫번째 매핑을 사용)
            // 본 시스템 아키텍처 상 로그인한 유저의 주 사용 site 정보가 필요할 수 있으나, 단일 쇼핑몰이라면 첫번째를 채택
            List<com.nanum.domain.product.model.ProductSite> sites = productSiteRepository
                    .findByProduct(cart.getProduct());
            if (!sites.isEmpty()) {
                com.nanum.domain.product.model.ProductSite siteInfo = sites.get(0);

                // Role(권한)에 따른 가격 分岐
                if (role == com.nanum.domain.member.model.MemberRole.ROLE_BIZ) {
                    calculatedBasePrice = siteInfo.getAPrice().intValue();
                } else if (role == com.nanum.domain.member.model.MemberRole.ROLE_VETERAN) {
                    calculatedBasePrice = siteInfo.getCPrice().intValue();
                } else {
                    // ROLE_USER 등 일반
                    calculatedBasePrice = siteInfo.getBPrice().intValue();
                }
            }

            // 람다 제약을 피하기 위해 effectively final 로 재할당
            final int basePrice = calculatedBasePrice;

            // 2. 옵션 존재 시 extraPrice 합산
            if (cart.getOptionId() != null && !cart.getProduct().getOptions().isEmpty()) {
                cart.getProduct().getOptions().stream()
                        .filter(opt -> opt.getId().equals(cart.getOptionId()))
                        .findFirst()
                        .ifPresent(opt -> {
                            response.setUnitPrice(basePrice + opt.getExtraPrice());
                            response.setOptionName(
                                    opt.getName1() + (opt.getName2() != null ? " " + opt.getName2() : ""));
                            response.setStockQuantity(opt.getStockQuantity());
                        });
            } else {
                response.setUnitPrice(basePrice);
                response.setOptionName(null);
                response.setStockQuantity(
                        cart.getProduct().getStockQuantity() != null ? cart.getProduct().getStockQuantity() : 0);
            }

            response.setTotalPrice(response.getUnitPrice() * cart.getQuantity());

            return response;
        }).toList();
    }

    /**
     * 장바구니 단건 삭제
     */
    @Transactional
    public void deleteCartItem(Long cartId, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니 항목을 찾을 수 없습니다."));

        if (!cart.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 장바구니만 삭제할 수 있습니다.");
        }

        cart.delete(memberId);
    }

    /**
     * 장바구니 복수/선택 삭제
     */
    @Transactional
    public void deleteCartItems(List<Long> cartIds, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<Cart> carts = cartRepository.findByCartIdInAndMemberMemberCode(cartIds, member.getMemberCode());
        for (Cart cart : carts) {
            cart.delete(memberId);
        }
    }

    /**
     * 장바구니 수량 변경
     */
    @Transactional
    public void updateCartQuantity(Long cartId, int quantity, String memberId) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니 항목을 찾을 수 없습니다."));

        if (!cart.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 장바구니만 수정할 수 있습니다.");
        }

        cart.updateQuantity(quantity);
    }
}

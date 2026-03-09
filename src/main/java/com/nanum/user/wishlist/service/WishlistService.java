package com.nanum.user.wishlist.service;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductCategory;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.wishlist.model.Wishlist;
import com.nanum.domain.wishlist.repository.WishlistRepository;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

        private final WishlistRepository wishlistRepository;
        private final MemberRepository memberRepository;
        private final ProductRepository productRepository;
        private final com.nanum.domain.file.service.FileService fileService;

        @Transactional
        public boolean toggleWishlist(String memberId, Long productId) {
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                if (wishlistRepository.existsByMemberMemberCodeAndProduct_Id(member.getMemberCode(), productId)) {
                        wishlistRepository.deleteByMemberMemberCodeAndProduct_Id(member.getMemberCode(), productId);
                        return false; // Removed
                } else {
                        wishlistRepository.save(new Wishlist(member, product));
                        return true; // Added
                }
        }

        @Transactional
        public void deleteWishlist(String memberId, Long productId) {
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

                wishlistRepository.deleteByMemberMemberCodeAndProduct_Id(member.getMemberCode(), productId);
        }

        public Page<ProductDTO.Response> getMyWishlist(String memberId, Pageable pageable) {
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

                Page<Wishlist> wishlistPage = wishlistRepository.findAllByMemberMemberCode(member.getMemberCode(),
                                pageable);

                List<ProductDTO.Response> productList = wishlistPage.getContent().stream()
                                .map(wishlist -> {
                                        Product p = wishlist.getProduct();
                                        List<ProductDTO.Image> images = fileService
                                                        .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT,
                                                                        String.valueOf(p.getId()))
                                                        .stream()
                                                        .map(f -> ProductDTO.Image.builder()
                                                                        .fileId(f.getFileId())
                                                                        .imageUrl(f.getPath())
                                                                        .type(f.getReferenceType().name())
                                                                        .displayOrder(f.getDisplayOrder())
                                                                        .build())
                                                        .collect(Collectors.toList());

                                        ProductCategory firstCategory = p.getCategories().isEmpty() ? null
                                                        : p.getCategories().get(0);

                                        return ProductDTO.Response.builder()
                                                        .productId(p.getId())
                                                        .categoryId(p.getCategories().stream()
                                                                        .map(ProductCategory::getCategoryId)
                                                                        .collect(Collectors.toList()))
                                                        .categoryName(firstCategory != null
                                                                        ? firstCategory.getCategoryName()
                                                                        : null)
                                                        .name(p.getName())
                                                        .mapPrice(p.getMapPrice())
                                                        .standardPrice(p.getStandardPrice())
                                                        .status(p.getStatus())
                                                        .images(images)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return new PageImpl<>(productList, pageable, wishlistPage.getTotalElements());
        }
}

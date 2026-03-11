package com.nanum.user.wishlist.service;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.member.model.MemberRole;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductSite;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.product.repository.ProductSiteRepository;
import com.nanum.domain.wishlist.dto.WishlistDTO;
import com.nanum.domain.wishlist.model.Wishlist;
import com.nanum.domain.wishlist.repository.WishlistRepository;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.repository.FileStoreRepository;
import com.nanum.domain.file.model.ReferenceType;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

        private final WishlistRepository wishlistRepository;
        private final MemberRepository memberRepository;
        private final ProductRepository productRepository;
        private final ProductSiteRepository productSiteRepository;
        private final FileStoreRepository fileStoreRepository;

        @Transactional
        public boolean toggleWishlist(String memberId, Long productId) {
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                // 현재 유효한 찜 상태 확인
                Optional<Wishlist> existing = wishlistRepository
                                .findByMemberMemberCodeAndProduct_Id(member.getMemberCode(), productId);

                if (existing.isPresent()) {
                        Wishlist wl = existing.get();
                        wl.delete(member.getMemberCode());
                        return false;
                } else {
                        // 과거에 삭제되었던 내역이 있는지 먼저 복구 시도
                        boolean deletedExists = wishlistRepository
                                        .existsDeletedByMemberAndProduct(member.getMemberCode(), productId);
                        if (deletedExists) {
                                wishlistRepository.restoreWishlist(member.getMemberCode(), productId);
                        } else {
                                wishlistRepository.save(new Wishlist(member, product));
                        }
                        return true;
                }
        }

        @Transactional
        public void deleteWishlist(String memberId, Long productId) {
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

                Wishlist wishlist = wishlistRepository
                                .findByMemberMemberCodeAndProduct_Id(member.getMemberCode(), productId)
                                .orElseThrow(() -> new IllegalArgumentException("찜 내역을 찾을 수 없습니다."));

                wishlist.delete(member.getMemberCode());
        }

        public Page<WishlistDTO.Response> getMyWishlist(String memberId, Pageable pageable) {
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
                MemberRole role = member.getRole();

                Page<Wishlist> wishlistPage = wishlistRepository
                                .findWishlistWithDetailsByMemberCode(member.getMemberCode(), pageable);

                List<String> productIds = wishlistPage.getContent().stream()
                                .map(w -> String.valueOf(w.getProduct().getId()))
                                .collect(Collectors.toList());

                Map<String, String> thumbMap;
                if (productIds.isEmpty()) {
                        thumbMap = java.util.Collections.emptyMap();
                } else {
                        thumbMap = fileStoreRepository
                                        .findByReferenceTypeAndReferenceIdIn(ReferenceType.PRODUCT, productIds)
                                        .stream()
                                        .filter(fs -> "Y".equals(fs.getIsMain()) && "N".equals(fs.getDeleteYn()))
                                        .collect(Collectors.toMap(FileStore::getReferenceId, FileStore::getPath,
                                                        (p1, p2) -> p1));
                }

                List<WishlistDTO.Response> responseList = wishlistPage.getContent().stream()
                                .map(wishlist -> {
                                        Product p = wishlist.getProduct();
                                        int calculatedBasePrice = p.getRetailPrice() != null ? p.getRetailPrice() : 0;

                                        List<ProductSite> sites = productSiteRepository.findByProduct(p);
                                        if (!sites.isEmpty()) {
                                                ProductSite siteInfo = sites.get(0);
                                                if (role == MemberRole.ROLE_BIZ) {
                                                        calculatedBasePrice = siteInfo.getAPrice() != null
                                                                        ? siteInfo.getAPrice().intValue()
                                                                        : 0;
                                                } else if (role == MemberRole.ROLE_VETERAN) {
                                                        calculatedBasePrice = siteInfo.getCPrice() != null
                                                                        ? siteInfo.getCPrice().intValue()
                                                                        : 0;
                                                } else {
                                                        calculatedBasePrice = siteInfo.getBPrice() != null
                                                                        ? siteInfo.getBPrice().intValue()
                                                                        : 0;
                                                }
                                        }

                                        return WishlistDTO.Response.builder()
                                                        .wishlistId(wishlist.getWishlistId())
                                                        .productId(p.getId())
                                                        .productName(p.getName())
                                                        .thumbnailUrl(thumbMap.get(String.valueOf(p.getId())))
                                                        .retailPrice(p.getRetailPrice() != null ? p.getRetailPrice()
                                                                        : 0)
                                                        .unitPrice(calculatedBasePrice)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return new PageImpl<>(responseList, pageable, wishlistPage.getTotalElements());
        }
}

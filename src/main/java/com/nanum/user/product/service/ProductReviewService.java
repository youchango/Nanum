package com.nanum.user.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.global.error.ErrorCode;
import com.nanum.domain.product.dto.ProductReviewDTO;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductReview;
import com.nanum.domain.product.model.ProductReviewLike;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.product.repository.ProductReviewRepository;
import com.nanum.domain.product.repository.ProductReviewLikeRepository;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewLikeRepository productReviewLikeRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public Page<ProductReviewDTO.Response> getReviews(Long productId, String currentMemberCode, Pageable pageable) {
        return productReviewRepository.findByProductIdAndDeleteYn(productId, "N", pageable)
                .map(review -> {
                    boolean isLiked = false;
                    if (currentMemberCode != null) {
                        isLiked = productReviewLikeRepository.existsByProductReviewIdAndMemberMemberCode(review.getId(),
                                currentMemberCode);
                    }
                    return ProductReviewDTO.Response.builder()
                            .reviewId(review.getId())
                            .productId(review.getProduct().getId())
                            .memberCode(review.getMember().getMemberCode())
                            .memberName(maskMemberName(review.getMember().getMemberName()))
                            .title(review.getTitle())
                            .content(review.getContent())
                            .rating(review.getRating())
                            .likeCount(review.getLikeCount())
                            .isLiked(isLiked)
                            .createdAt(review.getCreatedAt())
                            .updatedAt(review.getUpdatedAt())
                            .build();
                });
    }

    @Transactional
    public Long createReview(Long productId, String memberCode, ProductReviewDTO.Request request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        ProductReview review = ProductReview.builder()
                .product(product)
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .rating(request.getRating())
                .likeCount(0)
                .build();

        return productReviewRepository.save(review).getId();
    }

    @Transactional
    public void updateReview(Long reviewId, String memberCode, ProductReviewDTO.Request request) {
        ProductReview review = productReviewRepository.findByIdAndDeleteYn(reviewId, "N")
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!review.getMember().getMemberCode().equals(memberCode)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        review.updateInfo(request.getTitle(), request.getContent(), request.getRating());
    }

    @Transactional
    public void deleteReview(Long reviewId, String memberCode) {
        ProductReview review = productReviewRepository.findByIdAndDeleteYn(reviewId, "N")
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!review.getMember().getMemberCode().equals(memberCode)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        review.delete(memberCode);
    }

    @Transactional
    public void toggleLike(Long reviewId, String memberCode, boolean isLike) {
        ProductReview review = productReviewRepository.findByIdAndDeleteYn(reviewId, "N")
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        productReviewLikeRepository.findByProductReviewIdAndMemberMemberCode(reviewId, memberCode)
                .ifPresentOrElse(
                        like -> {
                            if (!isLike) {
                                productReviewLikeRepository.delete(like);
                                review.removeLike();
                            }
                        },
                        () -> {
                            if (isLike) {
                                Member member = memberRepository.findByMemberCode(memberCode)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
                                ProductReviewLike newLike = ProductReviewLike.builder()
                                        .productReview(review)
                                        .member(member)
                                        .build();
                                productReviewLikeRepository.save(newLike);
                                review.addLike();
                            }
                        });
    }

    private String maskMemberName(String name) {
        if (name == null || name.length() < 2)
            return name;
        if (name.length() == 2)
            return name.substring(0, 1) + "*";
        return name.substring(0, 1) + "*".repeat(name.length() - 2) + name.substring(name.length() - 1);
    }
}

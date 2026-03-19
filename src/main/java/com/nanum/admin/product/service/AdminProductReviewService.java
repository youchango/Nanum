package com.nanum.admin.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.global.error.ErrorCode;
import com.nanum.domain.product.dto.AdminProductReviewDTO;
import com.nanum.domain.product.dto.AdminProductReviewSearchDTO;
import com.nanum.domain.product.model.ProductReview;
import com.nanum.domain.product.repository.ProductReviewRepository;
import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    public Page<AdminProductReviewDTO.Response> getReviews(AdminProductReviewSearchDTO searchDTO, Pageable pageable) {
        return productReviewRepository.findByDeleteYn("N", pageable)
                .map(review -> AdminProductReviewDTO.Response.builder()
                        .reviewId(review.getId())
                        .productId(review.getProduct().getId())
                        .productName(review.getProduct().getName())
                        .memberCode(review.getMember().getMemberCode())
                        .memberName(review.getMember().getMemberName())
                        .title(review.getTitle())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .likeCount(review.getLikeCount())
                        .createdAt(review.getCreatedAt())
                        .build());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Manager manager = getCurrentManager();
        ProductReview review = productReviewRepository.findByIdAndDeleteYn(reviewId, "N")
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        review.delete(manager.getManagerCode());
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}

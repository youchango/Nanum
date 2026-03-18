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
import com.nanum.domain.order.model.OrderDetail;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.order.model.OrderStatus;
import com.nanum.user.order.repository.OrderDetailRepository;
import com.nanum.user.order.repository.OrderRepository;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewLikeRepository productReviewLikeRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

    public Page<ProductReviewDTO.Response> getReviews(Long productId, String currentMemberCode, Pageable pageable) {
        Page<ProductReview> page = productReviewRepository.findByProductIdAndDeleteYn(productId, "N", pageable);

        List<Long> reviewIds = page.getContent().stream().map(ProductReview::getId).toList();
        final Set<Long> likedReviewIds = currentMemberCode != null
                ? productReviewLikeRepository.findByProductReviewIdInAndMemberMemberCode(reviewIds, currentMemberCode)
                    .stream().map(l -> l.getProductReview().getId()).collect(Collectors.toSet())
                : Set.of();

        return page.map(review -> ProductReviewDTO.Response.builder()
                        .reviewId(review.getId())
                        .productId(review.getProduct().getId())
                        .memberCode(review.getMember().getMemberCode())
                        .memberName(maskMemberName(review.getMember().getMemberName()))
                        .title(review.getTitle())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .likeCount(review.getLikeCount())
                        .isLiked(likedReviewIds.contains(review.getId()))
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .build());
    }

    @Transactional
    public Long createReview(Long productId, String memberCode, ProductReviewDTO.Request request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // orderId 필수 검증
        if (request.getOrderId() == null) {
            throw new BusinessException("주문 정보가 필요합니다. 주문 내역에서 후기를 작성해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 제목/내용 검증
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("제목을 입력해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (request.getContent() == null || request.getContent().trim().length() < 10) {
            throw new BusinessException("내용을 10자 이상 입력해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 별점 범위 검증
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BusinessException("별점은 1~5 사이로 입력해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문 존재 + 본인 주문 검증
        OrderMaster order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("주문 정보를 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));
        if (!order.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 배송완료 상태 검증
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("배송 완료된 주문만 후기를 작성할 수 있습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 해당 상품이 주문에 포함되어 있는지 검증
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(request.getOrderId());
        boolean productInOrder = orderDetails.stream()
                .anyMatch(d -> d.getProductId().equals(productId));
        if (!productInOrder) {
            throw new BusinessException("해당 주문에 포함되지 않은 상품입니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문+상품 조합 중복 리뷰 체크
        if (productReviewRepository.existsByOrderIdAndProductIdAndDeleteYn(request.getOrderId(), productId, "N")) {
            throw new BusinessException("이미 해당 주문 건에 대한 후기가 작성되었습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        ProductReview review = ProductReview.builder()
                .product(product)
                .member(member)
                .orderId(request.getOrderId())
                .title(request.getTitle())
                .content(request.getContent())
                .rating(request.getRating())
                .likeCount(0)
                .build();

        productReviewRepository.save(review);

        // OrderDetail의 reviewYn 업데이트
        orderDetails.stream()
                .filter(d -> d.getProductId().equals(productId))
                .findFirst()
                .ifPresent(d -> d.setReviewYn("Y"));

        return review.getId();
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

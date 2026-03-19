package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.ProductReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewLikeRepository extends JpaRepository<ProductReviewLike, Long> {

    // 특정 리뷰에 회원이 좋아요를 눌렀는지 확인
    boolean existsByProductReviewIdAndMemberMemberCode(Long reviewId, String memberCode);

    // 특정 리뷰에 대한 회원의 좋아요 엔티티 조회
    Optional<ProductReviewLike> findByProductReviewIdAndMemberMemberCode(Long reviewId, String memberCode);

    // 여러 리뷰에 대한 회원의 좋아요 일괄 조회
    List<ProductReviewLike> findByProductReviewIdInAndMemberMemberCode(List<Long> reviewIds, String memberCode);
}

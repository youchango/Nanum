package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    // 상품별 리뷰 목록 조회 (삭제되지 않은 것)
    Page<ProductReview> findByProductIdAndDeleteYn(Long productId, String deleteYn, Pageable pageable);

    // 전체 마스터용 리뷰 목록 조회 (삭제되지 않은 것)
    Page<ProductReview> findByDeleteYn(String deleteYn, Pageable pageable);

    // 삭제되지 않은 단일 리뷰 조회
    Optional<ProductReview> findByIdAndDeleteYn(Long id, String deleteYn);
}

package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.ProductOptionSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionSiteRepository extends JpaRepository<ProductOptionSite, Long> {
    List<ProductOptionSite> findByProductSitePsId(Long psId);

    List<ProductOptionSite> findByProductOption(com.nanum.domain.product.model.ProductOption productOption);

    /**
     * 부모 상품 옵션 삭제 전, 연관된 사이트별 옵션 노출 정보를 모두 삭제합니다.
     * (외래키 제약조건 위배 방지 목적)
     *
     * @param productOption 기준 상품 옵션 메타데이터
     */
    void deleteByProductOption(com.nanum.domain.product.model.ProductOption productOption);
}

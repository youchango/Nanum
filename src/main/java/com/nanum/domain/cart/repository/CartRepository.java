package com.nanum.domain.cart.repository;

import com.nanum.domain.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberMemberCodeAndProduct_IdAndOptionIdAndDeleteYn(String memberCode, Long productId, Long optionId, String deleteYn);

    /**
     * 로그인한 사용자의 장바구니 목록과 상품, 옵션, 사이트 가격, 썸네일 등을 단일 쿼리로 함께 조회합니다.
     * 상품과 옵션, 그리고 Role에 따른 가격 계산 로직을 수행할 수 있도록 엔티티들을 즉시 로딩합니다.
     */
    @Query("SELECT c " +
            "FROM Cart c " +
            "JOIN FETCH c.product p " +
            "LEFT JOIN FETCH ProductSite ps ON p.id = ps.product.id " + // 사이트 할인가격 조건 부분 제거됨
            "LEFT JOIN FETCH ProductOption po ON c.optionId = po.id " +
            "LEFT JOIN FETCH FileStore fs ON CAST(p.id AS string) = fs.referenceId AND fs.referenceType = 'PRODUCT' AND fs.isMain = 'Y' AND fs.deleteYn = 'N' "
            +
            "WHERE c.member.memberCode = :memberCode " +
            "AND c.deleteYn = 'N' " +
            "ORDER BY c.cartId DESC")
    List<Cart> findCartListWithDetailsByMemberCode(@Param("memberCode") String memberCode);

    List<Cart> findByCartIdInAndMemberMemberCodeAndDeleteYn(List<Long> cartIds, String memberCode, String deleteYn);

    List<Cart> findByMemberMemberCodeAndProduct_IdInAndDeleteYn(String memberCode, List<Long> productIds, String deleteYn);
}

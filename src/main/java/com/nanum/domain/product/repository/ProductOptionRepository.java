package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProduct_Id(Long productId);

    /**
     * 옵션 가용재고 차감 (원자적 처리)
     * @return 업데이트된 행 수 (1이면 성공, 0이면 재고 부족)
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE product_option SET stock_quantity = stock_quantity - :quantity WHERE option_id = :optionId AND stock_quantity >= :quantity", nativeQuery = true)
    int decreaseStockQuantity(@org.springframework.data.repository.query.Param("optionId") Long optionId, @org.springframework.data.repository.query.Param("quantity") int quantity);

    /**
     * 옵션 가용재고 복원 (주문 취소 시)
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE product_option SET stock_quantity = stock_quantity + :quantity WHERE option_id = :optionId", nativeQuery = true)
    void increaseStockQuantity(@org.springframework.data.repository.query.Param("optionId") Long optionId, @org.springframework.data.repository.query.Param("quantity") int quantity);
}

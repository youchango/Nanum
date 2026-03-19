package com.nanum.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.nanum.domain.product.model.Product;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product>, ProductRepositoryCustom {
    boolean existsByCategoriesContainsAndDeleteYn(com.nanum.domain.product.model.ProductCategory category,
            String deleteYn);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.options WHERE p.id IN :ids")
    List<Product> findAllByIdWithOptions(@Param("ids") List<Long> ids);

    /**
     * 가용재고 차감 (원자적 처리)
     * @return 업데이트된 행 수 (1이면 성공, 0이면 재고 부족)
     */
    @Modifying
    @Query(value = "UPDATE product SET stock_quantity = stock_quantity - :quantity WHERE product_id = :productId AND stock_quantity >= :quantity", nativeQuery = true)
    int decreaseStockQuantity(@Param("productId") Long productId, @Param("quantity") int quantity);

    /**
     * 가용재고 복원 (주문 취소 시)
     */
    @Modifying
    @Query(value = "UPDATE product SET stock_quantity = stock_quantity + :quantity WHERE product_id = :productId", nativeQuery = true)
    void increaseStockQuantity(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Modifying
    @Query(value = "UPDATE product SET view_count = view_count + 1 WHERE product_id = :productId", nativeQuery = true)
    void increaseViewCount(@Param("productId") Long productId);
}

package com.nanum.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nanum.domain.product.model.ProductCategory;
import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    // 하위 카테고리 존재 여부 확인
    boolean existsByParent(ProductCategory parent);

    List<ProductCategory> findAllByOrderByDisplayOrderAsc();

}

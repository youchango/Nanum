package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductOption;
import com.nanum.domain.product.model.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    Optional<ProductStock> findByProductAndOption(Product product, ProductOption option);

    List<ProductStock> findByProduct(Product product);
}

package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductSiteRepository extends JpaRepository<ProductSite, Long> {
    Optional<ProductSite> findByProductAndSiteCd(Product product, String siteCd);

    boolean existsByProductAndSiteCd(Product product, String siteCd);
}

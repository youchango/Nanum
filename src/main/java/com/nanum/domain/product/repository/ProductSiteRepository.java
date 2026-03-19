package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductSiteRepository extends JpaRepository<ProductSite, Long> {
    List<ProductSite> findByProductAndSiteCd(Product product, String siteCd);

    List<ProductSite> findByProduct(Product product);

    boolean existsByProductAndSiteCd(Product product, String siteCd);

    List<ProductSite> findByProductInAndSiteCd(List<Product> products, String siteCd);
}

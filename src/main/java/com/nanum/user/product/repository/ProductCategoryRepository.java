package com.nanum.user.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.user.product.model.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}

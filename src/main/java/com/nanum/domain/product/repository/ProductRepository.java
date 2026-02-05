package com.nanum.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.nanum.domain.product.model.Product;

public interface ProductRepository
                extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product>, ProductRepositoryCustom {
}

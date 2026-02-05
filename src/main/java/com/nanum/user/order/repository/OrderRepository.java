package com.nanum.user.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.nanum.domain.order.model.OrderMaster;

public interface OrderRepository extends JpaRepository<OrderMaster, Long>, QuerydslPredicateExecutor<OrderMaster> {
}

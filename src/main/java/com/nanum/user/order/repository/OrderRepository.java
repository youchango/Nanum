package com.nanum.user.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.nanum.domain.order.model.OrderMaster;

public interface OrderRepository extends JpaRepository<OrderMaster, Long>, QuerydslPredicateExecutor<OrderMaster> {
    List<OrderMaster> findTop5BySiteCdOrderByCreatedAtDesc(String siteCd);

    List<OrderMaster> findTop5ByOrderByCreatedAtDesc();

    List<OrderMaster> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode);

    org.springframework.data.domain.Page<OrderMaster> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<OrderMaster> findByMemberMemberCodeAndCreatedAtBetweenOrderByCreatedAtDesc(
            String memberCode, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, org.springframework.data.domain.Pageable pageable);
}

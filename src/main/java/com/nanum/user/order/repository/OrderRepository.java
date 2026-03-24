package com.nanum.user.order.repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.domain.order.model.OrderMaster;

public interface OrderRepository extends JpaRepository<OrderMaster, Long>, QuerydslPredicateExecutor<OrderMaster> {
    List<OrderMaster> findTop5BySiteCdOrderByCreatedAtDesc(String siteCd);

    List<OrderMaster> findTop5ByOrderByCreatedAtDesc();

    Optional<OrderMaster> findByOrderNo(String orderNo);

    List<OrderMaster> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode);

    Page<OrderMaster> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode, Pageable pageable);

    Page<OrderMaster> findByMemberMemberCodeAndCreatedAtBetweenOrderByCreatedAtDesc(
            String memberCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}

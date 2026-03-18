package com.nanum.user.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.order.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    List<OrderDetail> findByOrderIdIn(List<Long> orderIds);
}

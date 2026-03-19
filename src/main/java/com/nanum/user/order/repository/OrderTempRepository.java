package com.nanum.user.order.repository;

import com.nanum.domain.order.model.OrderTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderTempRepository extends JpaRepository<OrderTemp, Long> {

    Optional<OrderTemp> findByOrderNoAndStatus(String orderNo, String status);

    Optional<OrderTemp> findByOrderNo(String orderNo);

    List<OrderTemp> findByStatusAndCreatedAtBefore(String status, LocalDateTime before);
}

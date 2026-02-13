package com.nanum.user.delivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.delivery.model.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findTop5ByOrderByCreatedAtDesc();
}

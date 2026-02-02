package com.nanum.user.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.user.delivery.model.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}

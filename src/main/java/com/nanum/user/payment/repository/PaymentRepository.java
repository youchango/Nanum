package com.nanum.user.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.user.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
}

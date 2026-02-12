package com.nanum.user.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.payment.model.PaymentMaster;

public interface PaymentRepository extends JpaRepository<PaymentMaster, Long>, PaymentRepositoryCustom {
}

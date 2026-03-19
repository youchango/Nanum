package com.nanum.user.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.payment.model.PaymentMaster;

public interface PaymentRepository extends JpaRepository<PaymentMaster, Long>, PaymentRepositoryCustom {
    List<PaymentMaster> findTop5ByOrderMaster_SiteCdOrderByPaymentDateDesc(String siteCd);

    List<PaymentMaster> findTop5ByOrderByPaymentDateDesc();

    Optional<PaymentMaster> findByOrderMasterOrderId(Long orderId);
}

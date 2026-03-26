package com.nanum.user.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.payment.model.Payment;
 
public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
    List<Payment> findTop5ByOrderMaster_SiteCdOrderByPaymentDateDesc(String siteCd);
 
    List<Payment> findTop5ByOrderByPaymentDateDesc();
 
    List<Payment> findByOrderMasterOrderId(Long orderId);

    List<Payment> findByOrderMasterOrderIdAndSiteCdAndOrderNo(Long orderId, String siteCd, String orderNo);
}

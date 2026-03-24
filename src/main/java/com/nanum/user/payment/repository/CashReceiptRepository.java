package com.nanum.user.payment.repository;

import com.nanum.domain.payment.model.CashReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashReceiptRepository extends JpaRepository<CashReceipt, Long>, CashReceiptRepositoryCustom {
}

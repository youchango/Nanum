package com.nanum.user.payment.repository;

import com.nanum.domain.payment.model.TaxInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxInvoiceRepository extends JpaRepository<TaxInvoice, Long>, TaxInvoiceRepositoryCustom {
}

package com.nanum.domain.payment.repository;

import com.nanum.domain.payment.model.TaxBillApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxBillApplyRepository extends JpaRepository<TaxBillApply, Long> {

    List<TaxBillApply> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode);

    Page<TaxBillApply> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode, Pageable pageable);

    Optional<TaxBillApply> findByIdAndMemberMemberCode(Long id, String memberCode);

    Optional<TaxBillApply> findByOrderIdAndMemberMemberCode(Long orderId, String memberCode);
}

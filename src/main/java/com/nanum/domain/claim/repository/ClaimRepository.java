package com.nanum.domain.claim.repository;

import com.nanum.domain.claim.model.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Page<Claim> findByMemberMemberCodeOrderByRequestedAtDesc(String memberCode, Pageable pageable);

    Optional<Claim> findByClaimIdAndMemberMemberCode(Long claimId, String memberCode);

    List<Claim> findByOrderMasterOrderId(Long orderId);

    boolean existsByOrderDetailIdAndClaimStatusNot(Long orderDetailId, String status);

    // Admin dashboard queries
    List<Claim> findTop5BySiteCdOrderByRequestedAtDesc(String siteCd);

    List<Claim> findTop5ByOrderByRequestedAtDesc();
}

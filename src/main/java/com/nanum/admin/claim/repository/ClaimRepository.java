package com.nanum.admin.claim.repository;

import com.nanum.admin.claim.entity.Claim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long>, QuerydslPredicateExecutor<Claim> {
    List<Claim> findTop5BySiteCdOrderByClaimDateEntryDesc(String siteCd);

    List<Claim> findTop5ByOrderByClaimDateEntryDesc();
}

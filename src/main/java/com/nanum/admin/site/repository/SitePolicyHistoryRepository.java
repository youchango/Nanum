package com.nanum.admin.site.repository;

import com.nanum.domain.site.model.SitePolicyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SitePolicyHistoryRepository extends JpaRepository<SitePolicyHistory, Long> {
    Optional<SitePolicyHistory> findTopBySiteCdOrderByCreatedAtDesc(String siteCd);
}

package com.nanum.admin.site.repository;

import com.nanum.domain.site.model.SitePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SitePolicyRepository extends JpaRepository<SitePolicy, Long> {
    Optional<SitePolicy> findBySiteCd(String siteCd);
}

package com.nanum.domain.banner.repository;

import com.nanum.domain.banner.model.Banner;
import com.nanum.domain.banner.model.BannerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByTypeAndSiteCd(BannerType type, String siteCd);
}

package com.nanum.user.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.banner.model.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer>, BannerRepositoryCustom {
}

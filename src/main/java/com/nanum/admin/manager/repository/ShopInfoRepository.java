package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.ShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopInfoRepository extends JpaRepository<ShopInfo, Integer> {
}

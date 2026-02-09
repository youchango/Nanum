package com.nanum.domain.shop.repository;

import com.nanum.domain.shop.model.ShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopInfoRepository extends JpaRepository<ShopInfo, Long> {
    boolean existsBySiteCd(String siteCd);

    ShopInfo findBySiteCd(String siteCd);
}

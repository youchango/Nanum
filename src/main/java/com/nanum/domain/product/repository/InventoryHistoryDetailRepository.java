package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.InventoryHistoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryHistoryDetailRepository extends JpaRepository<InventoryHistoryDetail, Long> {
}

package com.nanum.domain.product.repository;

import com.nanum.domain.product.model.InventoryHistoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryHistoryMasterRepository extends JpaRepository<InventoryHistoryMaster, Long> {
}

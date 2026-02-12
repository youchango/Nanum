package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.ManagerAuthGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerAuthGroupRepository extends JpaRepository<ManagerAuthGroup, Long> {
}

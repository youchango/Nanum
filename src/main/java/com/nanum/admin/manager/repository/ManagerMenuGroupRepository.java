package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.ManagerMenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerMenuGroupRepository extends JpaRepository<ManagerMenuGroup, Integer> {
}

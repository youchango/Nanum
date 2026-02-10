package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.ManagerMenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.admin.manager.entity.ManagerMenuGroupId;

@Repository
public interface ManagerMenuGroupRepository extends JpaRepository<ManagerMenuGroup, ManagerMenuGroupId> {
}

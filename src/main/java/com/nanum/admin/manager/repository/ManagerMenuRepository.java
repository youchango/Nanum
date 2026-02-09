package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.ManagerMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerMenuRepository extends JpaRepository<ManagerMenu, Integer> {
}

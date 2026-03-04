package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.ManagerScm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerScmRepository extends JpaRepository<ManagerScm, Long> {
}

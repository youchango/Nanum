package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long>, ManagerRepositoryCustom {
    Optional<Manager> findByManagerId(String managerId);

    Optional<Manager> findTopByManagerCodeStartingWithOrderByManagerCodeDesc(String prefix);

    List<Manager> findAllByApplyYn(String applyYn);

    @Query("SELECT m FROM Manager m JOIN FETCH m.authGroup WHERE m.applyYn = :applyYn")
    List<Manager> findAllByApplyYnWithAuthGroup(String applyYn);

    List<Manager> findAll();
}

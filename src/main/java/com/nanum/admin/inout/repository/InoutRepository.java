package com.nanum.admin.inout.repository;

import com.nanum.domain.inout.model.InoutMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

/**
 * 입출고 Master 리포지토리
 */
public interface InoutRepository extends JpaRepository<InoutMaster, Long>, QuerydslPredicateExecutor<InoutMaster> {
    Optional<InoutMaster> findByIoCode(String ioCode);
    
    /**
     * 마지막 io_code 번호 조회 (IO + 8자리 시퀀스 생성을 위함)
     */
    Optional<InoutMaster> findFirstByOrderByIoCodeDesc();
}

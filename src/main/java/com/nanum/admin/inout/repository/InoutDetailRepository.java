package com.nanum.admin.inout.repository;

import com.nanum.domain.inout.model.InoutDetail;
import com.nanum.domain.inout.model.InoutDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

/**
 * 입출고 상세 리포지토리
 */
public interface InoutDetailRepository extends JpaRepository<InoutDetail, InoutDetailId>, QuerydslPredicateExecutor<InoutDetail> {
    List<InoutDetail> findByIoCode(String ioCode);
    
    Optional<InoutDetail> findFirstByIoCodeOrderByNoDesc(String ioCode);
}

package com.nanum.admin.coupon.repository;

import com.nanum.domain.coupon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, QuerydslPredicateExecutor<Coupon> {
    
    @Query("SELECT c FROM Coupon c WHERE c.siteCd = :siteCd AND c.validEndDate >= CURRENT_TIMESTAMP AND c.validStartDate <= CURRENT_TIMESTAMP AND (c.targetMemberType = 'ALL' OR c.targetMemberType = :memberType)")
    List<Coupon> findDownloadableCoupons(@Param("memberType") String memberType, @Param("siteCd") String siteCd);
}

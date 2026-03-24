package com.nanum.domain.coupon.repository;

import com.nanum.domain.coupon.model.MemberCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberMemberCodeAndUsedYnOrderByIssuedAtDesc(String memberCode, String usedYn);

    Page<MemberCoupon> findByMemberMemberCodeAndUsedYnOrderByIssuedAtDesc(String memberCode, String usedYn, Pageable pageable);

    List<MemberCoupon> findByMemberMemberCodeOrderByIssuedAtDesc(String memberCode);

    Page<MemberCoupon> findByMemberMemberCodeOrderByIssuedAtDesc(String memberCode, Pageable pageable);

    Optional<MemberCoupon> findByIdAndMemberMemberCode(Long id, String memberCode);

    Optional<MemberCoupon> findByOrderIdAndUsedYn(Long orderId, String usedYn);

    int countByCouponIdAndMemberMemberCode(Long couponId, String memberCode);
}

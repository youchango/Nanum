package com.nanum.domain.coupon.repository;

import com.nanum.domain.coupon.model.MemberCoupon;
import com.nanum.domain.coupon.model.MemberCouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberMemberCodeAndStatusOrderByIssuedAtDesc(String memberCode, MemberCouponStatus status);

    Page<MemberCoupon> findByMemberMemberCodeAndStatusOrderByIssuedAtDesc(String memberCode, MemberCouponStatus status, Pageable pageable);

    List<MemberCoupon> findByMemberMemberCodeOrderByIssuedAtDesc(String memberCode);

    Page<MemberCoupon> findByMemberMemberCodeOrderByIssuedAtDesc(String memberCode, Pageable pageable);

    Optional<MemberCoupon> findByIssueIdAndMemberMemberCode(Long issueId, String memberCode);

    Optional<MemberCoupon> findByOrderIdAndStatus(Long orderId, MemberCouponStatus status);

    int countByCouponIdAndMemberMemberCode(Long couponId, String memberCode);
}

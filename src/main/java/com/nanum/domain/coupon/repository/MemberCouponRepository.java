package com.nanum.domain.coupon.repository;

import com.nanum.domain.coupon.model.MemberCoupon;
import com.nanum.domain.coupon.model.MemberCouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberMemberCodeAndSiteCdAndStatusOrderByIssuedAtDesc(String memberCode, String siteCd, MemberCouponStatus status);

    Page<MemberCoupon> findByMemberMemberCodeAndSiteCdAndStatusOrderByIssuedAtDesc(String memberCode, String siteCd, MemberCouponStatus status, Pageable pageable);

    List<MemberCoupon> findByMemberMemberCodeAndSiteCdOrderByIssuedAtDesc(String memberCode, String siteCd);

    Page<MemberCoupon> findByMemberMemberCodeAndSiteCdOrderByIssuedAtDesc(String memberCode, String siteCd, Pageable pageable);

    Optional<MemberCoupon> findByIssueIdAndMemberMemberCodeAndSiteCd(Long issueId, String memberCode, String siteCd);

    Optional<MemberCoupon> findByOrderIdAndStatus(Long orderId, MemberCouponStatus status);

    int countByCouponIdAndMemberMemberCodeAndSiteCd(Long couponId, String memberCode, String siteCd);
}

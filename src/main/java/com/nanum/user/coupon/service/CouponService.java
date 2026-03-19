package com.nanum.user.coupon.service;

import com.nanum.domain.coupon.dto.CouponDTO;
import com.nanum.domain.coupon.model.MemberCoupon;
import com.nanum.domain.coupon.repository.MemberCouponRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용 가능한 쿠폰 조회 (미사용 + 유효기간 내) - 페이징
     */
    public Page<CouponDTO.Response> getAvailableCoupons(String memberId, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return memberCouponRepository
                .findByMemberMemberCodeAndUsedYnOrderByIssuedAtDesc(member.getMemberCode(), "N", pageable)
                .map(CouponDTO.Response::from);
    }

    /**
     * 전체 쿠폰 조회 (사용 완료 포함) - 페이징
     */
    public Page<CouponDTO.Response> getAllCoupons(String memberId, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return memberCouponRepository
                .findByMemberMemberCodeOrderByIssuedAtDesc(member.getMemberCode(), pageable)
                .map(CouponDTO.Response::from);
    }
}

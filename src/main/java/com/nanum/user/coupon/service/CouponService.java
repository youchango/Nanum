package com.nanum.user.coupon.service;

import com.nanum.domain.coupon.dto.CouponDTO;
import com.nanum.domain.coupon.model.MemberCoupon;
import com.nanum.domain.coupon.repository.MemberCouponRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.admin.coupon.repository.CouponRepository;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import com.nanum.domain.coupon.model.MemberCouponStatus;
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

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용 가능한 쿠폰 조회 (미사용 + 유효기간 내) - 페이징
     */
    public Page<CouponDTO.Response> getAvailableCoupons(String memberId, String siteCd, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return memberCouponRepository
                .findByMemberMemberCodeAndSiteCdAndStatusOrderByIssuedAtDesc(member.getMemberCode(), siteCd, MemberCouponStatus.UNUSED, pageable)
                .map(CouponDTO.Response::from);
    }

    /**
     * 전체 쿠폰 조회 (사용 완료 포함) - 페이징
     */
    public Page<CouponDTO.Response> getAllCoupons(String memberId, String siteCd, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return memberCouponRepository
                .findByMemberMemberCodeAndSiteCdOrderByIssuedAtDesc(member.getMemberCode(), siteCd, pageable)
                .map(CouponDTO.Response::from);
    }

    /**
     * 다운로드 가능한 (이벤트/프로모션) 쿠폰 조회
     */
    public List<CouponDTO.DownloadableResponse> getDownloadableCoupons(String memberId, String siteCd) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String memberType = member.getMemberType() != null ? member.getMemberType().toString() : "U";

        // 현재 날짜 기준 발급 가능한 쿠폰 목록 조회 (등급 및 기간 반영)
        List<com.nanum.domain.coupon.model.Coupon> availableCoupons = couponRepository.findDownloadableCoupons(memberType, siteCd);

        return availableCoupons.stream().map(coupon -> {
            CouponDTO.DownloadableResponse response = CouponDTO.DownloadableResponse.from(coupon);

            // 총 발행 제한 체크
            boolean isGlobalLimitReached = coupon.getIssueLimit() != null && coupon.getIssueCount() >= coupon.getIssueLimit();

            // 개인 발급 제한 체크
            int myIssueCount = memberCouponRepository.countByCouponIdAndMemberMemberCodeAndSiteCd(coupon.getId(), member.getMemberCode(), siteCd);
            boolean isPersonalLimitReached = myIssueCount >= coupon.getMemberIssueLimit();

            response.setCanDownload(!isGlobalLimitReached && !isPersonalLimitReached);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 쿠폰 사용자가 직접 다운로드(발급)
     */
    @Transactional
    public void downloadCoupon(Long couponId, String memberId, String siteCd) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        com.nanum.domain.coupon.model.Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 유효 기간 검증 (추가 방어 로직)
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidStartDate()) || now.isAfter(coupon.getValidEndDate())) {
            throw new IllegalArgumentException("다운로드 가능한 기간이 아닙니다.");
        }

        // 대상 등급 검증
        String memberType = member.getMemberType() != null ? member.getMemberType().toString() : "U";
        if (!"ALL".equals(coupon.getTargetMemberType()) && !memberType.equals(coupon.getTargetMemberType())) {
            throw new IllegalArgumentException("회원님의 등급에서는 발급할 수 없는 쿠폰입니다.");
        }

        // 전체 총 발행 수량 한도 점검
        if (coupon.getIssueLimit() != null && coupon.getIssueCount() >= coupon.getIssueLimit()) {
            throw new IllegalArgumentException("아쉽지만 선착순 수량이 모두 소진되었습니다.");
        }

        // 개인당 발행 제한 점검
        int currentCount = memberCouponRepository.countByCouponIdAndMemberMemberCodeAndSiteCd(couponId, member.getMemberCode(), siteCd);
        if (currentCount >= coupon.getMemberIssueLimit()) {
            throw new IllegalArgumentException("이미 최대 인당 발급 횟수를 초과히였습니다.");
        }

        // 쿠폰 발급 (Insert)
        MemberCoupon newMemberCoupon = MemberCoupon.builder()
                .coupon(coupon)
                .member(member)
                .siteCd(siteCd)
                .status(MemberCouponStatus.UNUSED)
                .expiredAt(coupon.getValidEndDate())
                .build();
        memberCouponRepository.save(newMemberCoupon);

        // 쿠폰 마스터 발급 카운트 +1
        coupon.incrementIssueCount();
    }
}

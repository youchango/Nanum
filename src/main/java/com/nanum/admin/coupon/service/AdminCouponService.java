package com.nanum.admin.coupon.service;

import com.nanum.admin.coupon.dto.AdminCouponDTO;
import com.nanum.admin.coupon.repository.CouponRepository;
import com.nanum.domain.coupon.model.Coupon;
import com.nanum.domain.coupon.model.MemberCoupon;
import com.nanum.domain.coupon.repository.MemberCouponRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.member.model.MemberType;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nanum.domain.coupon.model.MemberCouponStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCouponService {

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;

    public List<AdminCouponDTO.Response> getCoupons(AdminCouponDTO.Search search) {
        // QueryDSL or simple findAll for bare minimum.
        // Assuming simple findAll for now. Search filters should be applied here.
        return couponRepository.findAll().stream()
                .filter(c -> search.getSiteCd() == null || search.getSiteCd().equals(c.getSiteCd()))
                .filter(c -> search.getSearchTargetMemberType() == null || "ALL".equals(search.getSearchTargetMemberType()) || search.getSearchTargetMemberType().equals(c.getTargetMemberType()))
                .filter(c -> search.getSearchKeyword() == null || c.getName().contains(search.getSearchKeyword()))
                .map(AdminCouponDTO.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createCoupon(AdminCouponDTO.Request request) {
        Coupon coupon = Coupon.builder()
                .siteCd(request.getSiteCd())
                .name(request.getName())
                .discountType(request.getDiscountType() != null ? request.getDiscountType() : "FIXED")
                .discountValue(request.getDiscountValue())
                .maxDiscount(request.getMaxDiscount())
                .minOrderPrice(request.getMinOrderPrice() != null ? request.getMinOrderPrice() : 0)
                .validStartDate(request.getValidStartDate())
                .validEndDate(request.getValidEndDate())
                .targetMemberType(request.getTargetMemberType() != null ? request.getTargetMemberType() : "ALL")
                .targetProductId(request.getTargetProductId())
                .issueLimit(request.getIssueLimit())
                .memberIssueLimit(request.getMemberIssueLimit() != null ? request.getMemberIssueLimit() : 1)
                .issueCount(0)
                .build();
        return couponRepository.save(coupon).getId();
    }

    @Transactional
    public void issueCoupon(Long couponId, AdminCouponDTO.IssueRequest issueRequest) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        List<Member> targetMembers = new ArrayList<>();

        if (issueRequest.isBulkIssueByGrade()) {
            if ("ALL".equals(coupon.getTargetMemberType())) {
                targetMembers = memberRepository.findAll();
            } else {
                targetMembers = memberRepository.findByMemberType(MemberType.valueOf(coupon.getTargetMemberType()));
            }
        } else if (issueRequest.getMemberCodes() != null && !issueRequest.getMemberCodes().isEmpty()) {
            targetMembers = memberRepository.findAllByMemberCodeIn(issueRequest.getMemberCodes());
        }

        if (targetMembers.isEmpty()) {
            throw new IllegalArgumentException("발급 대상 회원이 지정되지 않았습니다.");
        }

        for (Member member : targetMembers) {
            // Check issue limit
            if (coupon.getIssueLimit() != null && coupon.getIssueCount() >= coupon.getIssueLimit()) {
                break; // Stop issuing if total limit reached
            }

            // Check member issue limit
            int currentMemberCount = memberCouponRepository.countByCouponIdAndMemberMemberCodeAndSiteCd(couponId, member.getMemberCode(), coupon.getSiteCd());
            if (currentMemberCount >= coupon.getMemberIssueLimit()) {
                continue; // Skip member if their personal limit is reached
            }

            MemberCoupon memberCoupon = MemberCoupon.builder()
                    .coupon(coupon)
                    .member(member)
                    .siteCd(coupon.getSiteCd())
                    .status(MemberCouponStatus.UNUSED)
                    .expiredAt(coupon.getValidEndDate())
                    .build();
            memberCouponRepository.save(memberCoupon);
            
            coupon.incrementIssueCount();
        }
    }
}

package com.nanum.user.point.service;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.point.dto.PointDto;
import com.nanum.domain.point.model.Point;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    /**
     * 포인트 잔액 조회 (적립 합계 - 사용 합계)
     */
    public int getBalance(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return pointRepository.calculateBalance(member.getMemberCode());
    }

    /**
     * 포인트 거래 내역 조회 (최신순, 페이징)
     */
    public Page<PointDto> getHistory(String memberId, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Page<Point> pointPage = pointRepository.findByMemberMemberCodeOrderByCreatedAtDesc(
                member.getMemberCode(), pageable);
        return pointPage.map(PointDto::new);
    }
}

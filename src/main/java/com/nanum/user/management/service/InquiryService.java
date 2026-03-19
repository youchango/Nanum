package com.nanum.user.management.service;

import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.model.InquiryStatus;
import com.nanum.domain.inquiry.repository.InquiryRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    public Page<InquiryDTO.Response> getMyInquiries(String memberId, Pageable pageable) {
        Member writer = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return inquiryRepository.findByWriterAndDeleteYnOrderByCreatedAtDesc(writer, "N", pageable)
                .map(InquiryDTO.Response::from);
    }

    public InquiryDTO.Response getInquiry(Long id, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("문의를 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));

        if ("Y".equals(inquiry.getDeleteYn())) {
            throw new BusinessException("삭제된 문의입니다.", ErrorCode.ENTITY_NOT_FOUND);
        }

        if (!inquiry.getWriter().getMemberCode().equals(member.getMemberCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return InquiryDTO.Response.from(inquiry);
    }

    @Transactional
    public Long createInquiry(InquiryDTO.CreateRequest request, String memberId) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("제목을 입력해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (request.getContent() == null || request.getContent().trim().length() < 10) {
            throw new BusinessException("내용을 10자 이상 입력해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (request.getType() == null) {
            throw new BusinessException("문의 유형을 선택해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }

        Member writer = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Inquiry inquiry = Inquiry.builder()
                .type(request.getType())
                .productId(request.getProductId())
                .orderNo(request.getOrderNo())
                .title(request.getTitle())
                .content(request.getContent())
                .isSecret(request.getIsSecret() != null && "Y".equals(request.getIsSecret()) ? "Y" : "N")
                .writer(writer)
                .status(InquiryStatus.WAIT)
                .build();

        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    public Page<InquiryDTO.Response> getProductInquiries(Long productId, String memberId, Pageable pageable) {
        Page<Inquiry> page = inquiryRepository.findByProductIdAndDeleteYnOrderByCreatedAtDesc(productId, "N", pageable);

        String currentMemberCode = null;
        if (memberId != null) {
            currentMemberCode = memberRepository.findByMemberId(memberId)
                .map(Member::getMemberCode).orElse(null);
        }
        final String memberCode = currentMemberCode;

        return page.map(inquiry -> {
            InquiryDTO.Response resp = InquiryDTO.Response.from(inquiry);
            if ("Y".equals(inquiry.getIsSecret()) && (memberCode == null || !inquiry.getWriter().getMemberCode().equals(memberCode))) {
                resp.setContent("비밀글입니다.");
                resp.setAnswer(null);
            }
            return resp;
        });
    }
}

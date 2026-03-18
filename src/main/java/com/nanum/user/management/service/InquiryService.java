package com.nanum.user.management.service;

import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.model.InquiryStatus;
import com.nanum.domain.inquiry.repository.InquiryRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    public Page<InquiryDTO.Response> getMyInquiries(String memberId, Pageable pageable) {
        Member writer = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return inquiryRepository.findByWriterAndDeleteYnOrderByCreatedAtDesc(writer, "N", pageable)
                .map(InquiryDTO.Response::from);
    }

    public InquiryDTO.Response getInquiry(Long id, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));

        if ("Y".equals(inquiry.getDeleteYn())) {
            throw new IllegalArgumentException("삭제된 문의입니다.");
        }

        if (!inquiry.getWriter().getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 문의만 조회할 수 있습니다.");
        }

        return InquiryDTO.Response.from(inquiry);
    }

    @Transactional
    public Long createInquiry(InquiryDTO.CreateRequest request, String memberId) {
        Member writer = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Inquiry inquiry = Inquiry.builder()
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .productId(request.getProductId())
                .orderNo(request.getOrderNo())
                .isSecret(request.getIsSecret() != null && "Y".equals(request.getIsSecret()) ? "Y" : "N")
                .writer(writer)
                .status(InquiryStatus.WAIT)
                .build();

        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    public Page<InquiryDTO.Response> getProductInquiries(Long productId, String memberId, Pageable pageable) {
        // Get all inquiries for the product (not deleted)
        Page<Inquiry> page = inquiryRepository.findByProductIdAndDeleteYnOrderByCreatedAtDesc(productId, "N", pageable);

        // Get current member code (nullable for non-logged-in users)
        String currentMemberCode = null;
        if (memberId != null) {
            currentMemberCode = memberRepository.findByMemberId(memberId)
                .map(m -> m.getMemberCode()).orElse(null);
        }
        final String memberCode = currentMemberCode;

        return page.map(inquiry -> {
            InquiryDTO.Response resp = InquiryDTO.Response.from(inquiry);
            // If secret and not the writer, mask content
            if ("Y".equals(inquiry.getIsSecret()) && (memberCode == null || !inquiry.getWriter().getMemberCode().equals(memberCode))) {
                resp.setContent("비밀글입니다.");
                resp.setAnswer(null);
            }
            return resp;
        });
    }
}

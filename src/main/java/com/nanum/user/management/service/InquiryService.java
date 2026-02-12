package com.nanum.user.management.service;

import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.model.InquiryStatus;
import com.nanum.domain.inquiry.repository.InquiryRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

    public List<InquiryDTO.Response> getMyInquiries(String memberCode) {
        Member writer = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return inquiryRepository.findByWriter(writer).stream()
                .filter(i -> "N".equals(i.getDeleteYn()))
                .map(InquiryDTO.Response::from)
                .collect(Collectors.toList());
    }

    public InquiryDTO.Response getInquiry(Long id, String memberCode) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));

        if ("Y".equals(inquiry.getDeleteYn())) {
            throw new IllegalArgumentException("삭제된 문의입니다.");
        }

        if (!inquiry.getWriter().getMemberCode().equals(memberCode)) {
            throw new IllegalArgumentException("본인의 문의만 조회할 수 있습니다.");
        }

        return InquiryDTO.Response.from(inquiry);
    }

    @Transactional
    public Long createInquiry(InquiryDTO.CreateRequest request, String memberCode) {
        Member writer = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Inquiry inquiry = Inquiry.builder()
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .writer(writer)
                .status(InquiryStatus.WAIT)
                .build();

        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }
}

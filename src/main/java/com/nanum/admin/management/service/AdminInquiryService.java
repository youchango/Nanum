package com.nanum.admin.management.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.repository.InquiryRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminInquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    public Page<InquiryDTO.Response> getInquiries(InquiryDTO.Search search, Pageable pageable) {
        Manager manager = getCurrentManager();
        // MASTER 또는 SCM 권한이 아니면 자신의 사이트 코드 강제 설정
        if (manager.getMbType() != ManagerType.MASTER && manager.getMbType() != ManagerType.SCM) {
            search.setSiteCd(manager.getSiteCd());
        }

        return inquiryRepository.search(search, pageable)
                .map(InquiryDTO.Response::from);
    }

    public InquiryDTO.Response getInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        return InquiryDTO.Response.from(inquiry);
    }

    @Transactional
    public void replyInquiry(Long id, InquiryDTO.ReplyRequest request, String answererCode) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));

        Member answerer = memberRepository.findByMemberCode(answererCode)
                .orElseThrow(() -> new IllegalArgumentException("답변자 정보를 찾을 수 없습니다."));

        inquiry.reply(request.getAnswer(), answerer);
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}

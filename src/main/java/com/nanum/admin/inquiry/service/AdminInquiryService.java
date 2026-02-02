package com.nanum.admin.inquiry.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.inquiry.model.Inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminInquiryService {

    // private final InquiryMapper inquiryMapper; // QueryDSL 전환으로 제거
    private final com.nanum.user.inquiry.repository.InquiryRepository inquiryRepository;

    /**
     * 문의 목록 조회
     */
    public List<Inquiry> getInquiryList(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return inquiryRepository.searchInquiries(searchDTO, pageable).getContent();
    }

    /**
     * 문의 전체 개수 조회
     */
    public int getInquiryCount(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return (int) inquiryRepository.searchInquiries(searchDTO, pageable).getTotalElements();
    }

    /**
     * 문의 상세 조회
     */
    public Inquiry getInquiryDetail(int inquiryId) {
        return inquiryRepository.findById(inquiryId).orElse(null);
    }

    /**
     * 답변 등록 및 상태 변경
     */
    @Transactional
    public void registerAnswer(int inquiryId, String answer, Long answererId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        inquiry.setAnswer(answer);
        inquiry.setAnswererId(answererId);
        inquiry.setInquiryStatus(com.nanum.user.inquiry.model.InquiryStatus.ANSWERED);
        inquiry.setAnsweredAt(java.time.LocalDateTime.now());
    }
}

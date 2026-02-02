package com.nanum.user.inquiry.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.inquiry.model.Inquiry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {
    Page<Inquiry> searchInquiries(SearchDTO searchDTO, Pageable pageable);
}

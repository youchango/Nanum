package com.nanum.domain.inquiry.repository;

import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.domain.inquiry.model.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {
    Page<Inquiry> search(InquiryDTO.Search search, Pageable pageable);
}

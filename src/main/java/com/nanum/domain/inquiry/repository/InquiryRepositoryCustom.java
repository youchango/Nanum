package com.nanum.domain.inquiry.repository;

import com.nanum.domain.inquiry.dto.InquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {
    Page<InquiryDTO.Response> search(InquiryDTO.Search search, Pageable pageable);
}

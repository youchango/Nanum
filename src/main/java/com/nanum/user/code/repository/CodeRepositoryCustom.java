package com.nanum.user.code.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.code.model.Code;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodeRepositoryCustom {
    Page<Code> searchCodes(SearchDTO searchDTO, Pageable pageable);
}

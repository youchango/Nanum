package com.nanum.user.content.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.content.model.Content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {
    Page<Content> searchContents(SearchDTO searchDTO, Pageable pageable);
}

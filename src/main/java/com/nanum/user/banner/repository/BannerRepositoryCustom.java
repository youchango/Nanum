package com.nanum.user.banner.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.banner.model.Banner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BannerRepositoryCustom {
    Page<Banner> searchBanners(SearchDTO searchDTO, Pageable pageable);
}

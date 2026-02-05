package com.nanum.admin.management.service;

import com.nanum.domain.banner.dto.BannerDTO;
import com.nanum.domain.banner.model.Banner;
import com.nanum.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBannerService {

    private final BannerRepository bannerRepository;

    public List<BannerDTO.Response> getBanners() {
        return bannerRepository.findAll().stream()
                .map(BannerDTO.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createBanner(BannerDTO.Request request) {
        Banner banner = Banner.builder()
                .type(request.getType())
                .imageFile(request.getImageFile())
                .linkUrl(request.getLinkUrl())
                .sortOrder(request.getSortOrder())
                .deviceType(request.getDeviceType())
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .useYn(request.getUseYn())
                .deleteYn("N")
                .build();
        bannerRepository.save(banner);
        return banner.getId();
    }

    @Transactional
    public void updateBanner(Long id, BannerDTO.Request request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
        banner.update(request.getType(), request.getImageFile(), request.getLinkUrl(), request.getSortOrder(),
                request.getUseYn(), request.getStartDatetime(), request.getEndDatetime());
    }

    @Transactional
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }
}

package com.nanum.user.management.service;

import com.nanum.domain.banner.dto.BannerDTO;
import com.nanum.domain.banner.model.BannerType;
import com.nanum.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<BannerDTO.Response> getBanners(BannerType type) {
        // Ideally filter by UseYn='Y' and Date within range via Repository Query
        return bannerRepository.findByType(type).stream()
                .filter(b -> "Y".equals(b.getUseYn()))
                .filter(b -> {
                    LocalDateTime now = LocalDateTime.now();
                    return b.getStartDatetime().isBefore(now) && b.getEndDatetime().isAfter(now);
                })
                .sorted((b1, b2) -> Integer.compare(b1.getSortOrder(), b2.getSortOrder()))
                .map(BannerDTO.Response::from)
                .collect(Collectors.toList());
    }
}

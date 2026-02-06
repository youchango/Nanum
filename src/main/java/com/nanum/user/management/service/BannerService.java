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
    private final com.nanum.domain.file.service.FileService fileService;

    public List<BannerDTO.Response> getBanners(BannerType type) {
        // Ideally filter by UseYn='Y' and Date within range via Repository Query
        return bannerRepository.findByType(type).stream()
                .filter(b -> "N".equals(b.getDeleteYn()))
                .filter(b -> "Y".equals(b.getUseYn()))
                .filter(b -> {
                    LocalDateTime now = LocalDateTime.now();
                    return b.getStartDatetime().isBefore(now) && b.getEndDatetime().isAfter(now);
                })
                .sorted((b1, b2) -> Integer.compare(b1.getSortOrder(), b2.getSortOrder()))
                .map(banner -> {
                    BannerDTO.Response response = BannerDTO.Response.from(banner);
                    List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                            .getFiles(com.nanum.domain.file.model.ReferenceType.BANNER, String.valueOf(banner.getId()))
                            .stream()
                            .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                            .collect(Collectors.toList());
                    response.setFiles(files);
                    return response;
                })
                .collect(Collectors.toList());
    }
}

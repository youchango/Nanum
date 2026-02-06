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
    private final com.nanum.domain.file.service.FileService fileService;

    public List<BannerDTO.Response> getBanners() {
        return bannerRepository.findAll().stream()
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

    @Transactional
    public Long createBanner(BannerDTO.Request request, List<org.springframework.web.multipart.MultipartFile> files) {
        Banner banner = Banner.builder()
                .type(request.getType())
                .linkUrl(request.getLinkUrl())
                .sortOrder(request.getSortOrder())
                .deviceType(request.getDeviceType())
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .useYn(request.getUseYn())
                .deleteYn("N")
                .build();
        bannerRepository.save(banner);

        if (files != null && !files.isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                fileService.uploadFile(file, com.nanum.domain.file.model.ReferenceType.BANNER,
                        String.valueOf(banner.getId()), false);
            }
        }
        return banner.getId();
    }

    @Transactional
    public void updateBanner(Long id, BannerDTO.Request request,
            List<org.springframework.web.multipart.MultipartFile> files) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
        banner.update(request.getType(), request.getLinkUrl(), request.getSortOrder(),
                request.getUseYn(), request.getStartDatetime(), request.getEndDatetime());

        // 파일 추가 로직 (기존 파일 삭제 로직은 별도 API 또는 요청 플래그 필요하나, 여기서는 추가만 구현 일단)
        if (files != null && !files.isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                fileService.uploadFile(file, com.nanum.domain.file.model.ReferenceType.BANNER,
                        String.valueOf(banner.getId()), false);
            }
        }
    }

    @Transactional
    public void deleteBanner(Long id, String memberCode) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
        banner.delete(memberCode);
        fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.BANNER, String.valueOf(id), memberCode);
    }
}

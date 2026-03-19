package com.nanum.admin.management.service;

import com.nanum.domain.banner.dto.BannerDTO;
import com.nanum.domain.banner.dto.BannerSearchDTO;
import com.nanum.domain.banner.model.Banner;
import com.nanum.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBannerService {

    private final BannerRepository bannerRepository;
    private final com.nanum.domain.file.service.FileService fileService;

    /**
     * 배너 목록 조회.
     * 검색 조건(siteCd, useYn, 기간, 키워드)을 사용하여 필터링한 배너 목록을 반환합니다.
     *
     * @param searchDTO 배너 검색 조건 DTO
     * @return 검색된 배너 응답 DTO 목록
     */
    public List<BannerDTO.Response> getBanners(BannerSearchDTO searchDTO) {
        // 전체 조회 후 조건별 인메모리 필터링
        List<Banner> banners;
        if (StringUtils.hasText(searchDTO.getSiteCd())) {
            banners = bannerRepository.findBySiteCd(searchDTO.getSiteCd());
        } else {
            banners = bannerRepository.findAll();
        }

        return banners.stream()
                // 사용 여부 필터 (ALL이면 전체)
                .filter(b -> {
                    String useYn = searchDTO.getSearchUseYn();
                    return !StringUtils.hasText(useYn) || "ALL".equalsIgnoreCase(useYn) || useYn.equalsIgnoreCase(b.getUseYn());
                })
                // 게시 시작일 필터: searchStartDate 이후에 시작하는 배너
                .filter(b -> searchDTO.getSearchStartDate() == null
                        || !b.getStartDatetime().toLocalDate().isBefore(searchDTO.getSearchStartDate()))
                // 게시 종료일 필터: searchEndDate 이전에 종료하는 배너
                .filter(b -> searchDTO.getSearchEndDate() == null
                        || !b.getEndDatetime().toLocalDate().isAfter(searchDTO.getSearchEndDate()))
                // 키워드 필터 (현재 searchType: title만 지원)
                .filter(b -> {
                    String keyword = searchDTO.getSearchKeyword();
                    if (!StringUtils.hasText(keyword)) return true;
                    String type = searchDTO.getSearchType();
                    if ("title".equalsIgnoreCase(type) || !StringUtils.hasText(type)) {
                        return b.getTitle() != null && b.getTitle().contains(keyword);
                    }
                    return true;
                })
                .map(banner -> {
                    BannerDTO.Response response = BannerDTO.Response.from(banner);
                    // 이미지 파일 조회 및 전체 URL 변환
                    List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                            .getFiles(com.nanum.domain.file.model.ReferenceType.BANNER, String.valueOf(banner.getId()))
                            .stream()
                            .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                            .collect(Collectors.toList());
                    response.setFiles(files);
                    // 대표 이미지 URL 설정 (첫 번째 파일 또는 isMain=Y 파일)
                    fileService.getFiles(com.nanum.domain.file.model.ReferenceType.BANNER, String.valueOf(banner.getId()))
                            .stream()
                            .findFirst()
                            .ifPresent(f -> response.setImageUrl(fileService.getFullUrl(f.getPath())));
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 배너 등록.
     *
     * @param request 등록 요청 DTO
     * @param files   이미지 파일 목록
     * @return 등록된 배너 ID
     */
    @Transactional
    public Long createBanner(BannerDTO.Request request, List<org.springframework.web.multipart.MultipartFile> files) {
        Banner banner = Banner.builder()
                .title(request.getTitle())
                .siteCd(request.getSiteCd())
                .type(request.getType())
                .linkUrl(request.getLinkUrl())
                .sortOrder(request.getSortOrder())
                .deviceType(request.getDeviceType() != null ? request.getDeviceType() : "ALL")
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .useYn(request.getUseYn() != null ? request.getUseYn() : "Y")
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

    /**
     * 배너 수정.
     *
     * @param id      배너 ID
     * @param request 수정 요청 DTO
     * @param files   이미지 파일 목록 (선택)
     */
    @Transactional
    public void updateBanner(Long id, BannerDTO.Request request,
            List<org.springframework.web.multipart.MultipartFile> files) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
        banner.update(request.getTitle(), request.getSiteCd(), request.getType(), request.getLinkUrl(),
                request.getSortOrder(), request.getUseYn(), request.getStartDatetime(), request.getEndDatetime());

        // 새 파일이 있으면 기존 파일 삭제 후 신규 파일 업로드 (syncFiles로 교체)
        if (files != null && !files.isEmpty()) {
            // 기존 파일 모두 삭제
            List<com.nanum.domain.file.model.FileStore> existingFiles = fileService
                    .getFiles(com.nanum.domain.file.model.ReferenceType.BANNER, String.valueOf(banner.getId()));
            for (com.nanum.domain.file.model.FileStore existing : existingFiles) {
                fileService.deleteFile(existing.getFileId());
            }
            // 신규 파일 업로드
            for (org.springframework.web.multipart.MultipartFile file : files) {
                fileService.uploadFile(file, com.nanum.domain.file.model.ReferenceType.BANNER,
                        String.valueOf(banner.getId()), true);
            }
        }
    }

    /**
     * 배너 삭제 (논리 삭제).
     *
     * @param id         배너 ID
     * @param memberCode 삭제 처리자 코드
     */
    @Transactional
    public void deleteBanner(Long id, String memberCode) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
        banner.delete(memberCode);
        fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.BANNER, String.valueOf(id), memberCode);
    }
}

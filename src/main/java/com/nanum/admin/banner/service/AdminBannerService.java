package com.nanum.admin.banner.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.banner.model.Banner;
import com.nanum.user.banner.model.BannerDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBannerService {

    // private final BannerMapper bannerMapper; // QueryDSL 전환으로 제거
    private final com.nanum.user.banner.repository.BannerRepository bannerRepository;
    private final com.nanum.global.file.service.FileStorageService fileStorageService;

    /**
     * 배너 목록 조회
     */
    public List<Banner> getBannerList(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return bannerRepository.searchBanners(searchDTO, pageable).getContent();
    }

    /**
     * 배너 전체 개수 조회
     */
    public int getBannerCount(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return (int) bannerRepository.searchBanners(searchDTO, pageable).getTotalElements();
    }

    /**
     * 배너 상세 조회
     */
    public Banner getBannerDetail(int bannerId) {
        return bannerRepository.findById(bannerId).orElse(null);
    }

    /**
     * 배너 등록
     */
    @Transactional
    public void registerBanner(BannerDTO bannerDTO, Long createdBy) throws IOException {
        // DTO -> Entity 변환
        Banner banner = bannerDTO.toEntity();

        // 파일 업로드 처리
        if (bannerDTO.getImageFile() != null && !bannerDTO.getImageFile().isEmpty()) {
            String imagePath = fileStorageService.upload(bannerDTO.getImageFile(), "banner");
            banner.setImageFile(imagePath);
        } else {
            throw new IllegalArgumentException("배너 이미지는 필수입니다.");
        }

        banner.setCreatedBy(createdBy);
        bannerRepository.save(banner);
    }

    /**
     * 배너 수정
     */
    @Transactional
    public void updateBanner(BannerDTO bannerDTO, Long updatedBy) throws IOException {
        Banner banner = bannerRepository.findById(bannerDTO.getBannerId())
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));

        // DTO -> Entity 변환 (Dirty Checking)
        bannerDTO.updateEntity(banner);

        // 파일 업로드 처리 (새로운 파일이 있을 경우만)
        if (bannerDTO.getImageFile() != null && !bannerDTO.getImageFile().isEmpty()) {
            // 기존 파일 삭제 (선택 사항)
            // fileStorageService.delete(banner.getImageFile());

            String imagePath = fileStorageService.upload(bannerDTO.getImageFile(), "banner");
            banner.setImageFile(imagePath);
        }

        banner.setUpdatedBy(updatedBy);
    }

    /**
     * 배너 삭제
     */
    @Transactional
    public void deleteBanner(int bannerId, Long deletedBy) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));

        banner.setDeleteYn("Y");
        banner.setDeletedBy(deletedBy);
        // banner.setDeletedAt(LocalDateTime.now()); // PreUpdate or explicit
        banner.setDeletedAt(java.time.LocalDateTime.now());
    }
}

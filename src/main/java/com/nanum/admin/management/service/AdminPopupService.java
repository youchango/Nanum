package com.nanum.admin.management.service;

import com.nanum.domain.popup.dto.PopupDTO;
import com.nanum.domain.popup.dto.PopupSearchDTO;
import com.nanum.domain.popup.model.Popup;
import com.nanum.domain.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPopupService {

    private final PopupRepository popupRepository;
    private final com.nanum.domain.file.service.FileService fileService;

    /**
     * 팝업 목록 조회.
     * 검색 조건(siteCd, useYn, 기간, 키워드)을 사용하여 필터링된 팝업 목록을 반환합니다.
     *
     * @param searchDTO 팝업 검색 조건 DTO
     * @return 검색된 팝업 응답 DTO 목록
     */
    public List<PopupDTO.Response> getPopups(PopupSearchDTO searchDTO) {
        // 전체 조회 후 조건별 인메모리 필터링
        List<Popup> popups;
        if (StringUtils.hasText(searchDTO.getSiteCd())) {
            popups = popupRepository.findBySiteCd(searchDTO.getSiteCd());
        } else {
            popups = popupRepository.findAll();
        }

        return popups.stream()
                // 사용 여부 필터 (ALL이면 전체)
                .filter(p -> {
                    String useYn = searchDTO.getSearchUseYn();
                    return !StringUtils.hasText(useYn) || "ALL".equalsIgnoreCase(useYn) || useYn.equalsIgnoreCase(p.getUseYn());
                })
                // 노출 시작일 필터: searchStartDate 이후에 시작하는 팝업
                .filter(p -> searchDTO.getSearchStartDate() == null
                        || !p.getStartDatetime().toLocalDate().isBefore(searchDTO.getSearchStartDate()))
                // 노출 종료일 필터: searchEndDate 이전에 종료하는 팝업
                .filter(p -> searchDTO.getSearchEndDate() == null
                        || !p.getEndDatetime().toLocalDate().isAfter(searchDTO.getSearchEndDate()))
                // 키워드 필터 (현재 searchType: title만 지원)
                .filter(p -> {
                    String keyword = searchDTO.getSearchKeyword();
                    if (!StringUtils.hasText(keyword)) return true;
                    String type = searchDTO.getSearchType();
                    if ("title".equalsIgnoreCase(type) || !StringUtils.hasText(type)) {
                        return p.getTitle() != null && p.getTitle().contains(keyword);
                    }
                    return true;
                })
                .map(popup -> {
                    PopupDTO.Response response = PopupDTO.Response.from(popup);
                    // 이미지 파일 조회 및 전체 URL 변환
                    List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                            .getFiles(com.nanum.domain.file.model.ReferenceType.POPUP, String.valueOf(popup.getId()))
                            .stream()
                            .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                            .collect(Collectors.toList());
                    response.setFiles(files);
                    // 대표 이미지 URL 설정 (첫 번째 파일)
                    fileService.getFiles(com.nanum.domain.file.model.ReferenceType.POPUP, String.valueOf(popup.getId()))
                            .stream()
                            .findFirst()
                            .ifPresent(f -> response.setImageUrl(fileService.getFullUrl(f.getPath())));
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 팝업 등록.
     *
     * @param request 등록 요청 DTO
     * @param files   이미지 파일 목록 (선택)
     * @return 등록된 팝업 ID
     */
    @Transactional
    public Long createPopup(PopupDTO.Request request, List<org.springframework.web.multipart.MultipartFile> files) {
        Popup popup = Popup.builder()
                .siteCd(request.getSiteCd())
                .title(request.getTitle())
                .contentHtml(request.getContentHtml())
                .linkUrl(request.getLinkUrl())
                .width(request.getWidth())
                .height(request.getHeight())
                .posX(request.getPosX())
                .posY(request.getPosY())
                .closeType(request.getCloseType())
                .deviceType(request.getDeviceType() != null ? request.getDeviceType() : "ALL")
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .useYn(request.getUseYn() != null ? request.getUseYn() : "Y")
                .build();
        popupRepository.save(popup);

        if (files != null && !files.isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                fileService.uploadFile(file, com.nanum.domain.file.model.ReferenceType.POPUP,
                        String.valueOf(popup.getId()), false);
            }
        }
        return popup.getId();
    }

    /**
     * 팝업 수정.
     *
     * @param id      팝업 ID
     * @param request 수정 요청 DTO
     * @param files   이미지 파일 목록 (선택)
     */
    @Transactional
    public void updatePopup(Long id, PopupDTO.Request request,
            List<org.springframework.web.multipart.MultipartFile> files) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));
        popup.update(request.getTitle(), request.getLinkUrl(), request.getWidth(),
                request.getHeight(), request.getPosX(), request.getPosY(), request.getCloseType(),
                request.getStartDatetime(), request.getEndDatetime(), request.getUseYn());

        // 새 파일이 있으면 기존 파일 삭제 후 신규 파일 업로드
        if (files != null && !files.isEmpty()) {
            // 기존 파일 모두 삭제
            List<com.nanum.domain.file.model.FileStore> existingFiles = fileService
                    .getFiles(com.nanum.domain.file.model.ReferenceType.POPUP, String.valueOf(popup.getId()));
            for (com.nanum.domain.file.model.FileStore existing : existingFiles) {
                fileService.deleteFile(existing.getFileId());
            }
            // 신규 파일 업로드
            for (org.springframework.web.multipart.MultipartFile file : files) {
                fileService.uploadFile(file, com.nanum.domain.file.model.ReferenceType.POPUP,
                        String.valueOf(popup.getId()), true);
            }
        }
    }

    /**
     * 팝업 삭제 (논리 삭제).
     *
     * @param id         팝업 ID
     * @param memberCode 삭제 처리자 코드
     */
    @Transactional
    public void deletePopup(Long id, String memberCode) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));
        popup.delete(memberCode);
        fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.POPUP, String.valueOf(id), memberCode);
    }
}

package com.nanum.admin.popup.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.popup.model.Popup;
import com.nanum.user.popup.model.PopupDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPopupService {

    // private final PopupMapper popupMapper; // QueryDSL 전환으로 제거
    private final com.nanum.user.popup.repository.PopupRepository popupRepository;
    private final com.nanum.global.file.service.FileStorageService fileStorageService;

    /**
     * 팝업 목록 조회
     */
    public List<Popup> getPopupList(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return popupRepository.searchPopups(searchDTO, pageable).getContent();
    }

    /**
     * 팝업 전체 개수 조회
     */
    public int getPopupCount(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return (int) popupRepository.searchPopups(searchDTO, pageable).getTotalElements();
    }

    /**
     * 팝업 상세 조회
     */
    public Popup getPopupDetail(int popupId) {
        return popupRepository.findById(popupId).orElse(null);
    }

    /**
     * 팝업 등록
     */
    @Transactional
    public void registerPopup(PopupDTO popupDTO, Long createdBy) throws IOException {
        Popup popup = new Popup();

        // 파일 업로드 처리
        if (popupDTO.getImageFile() != null && !popupDTO.getImageFile().isEmpty()) {
            String imagePath = fileStorageService.upload(popupDTO.getImageFile(), "popup");
            popup.setContentImage(imagePath);
        }

        // DTO -> Entity 변환
        popup.setTitle(popupDTO.getTitle());
        popup.setContentHtml(popupDTO.getContentHtml());
        popup.setLinkType(popupDTO.getLinkType());
        popup.setLinkUrl(popupDTO.getLinkUrl());
        popup.setWidth(popupDTO.getWidth());
        popup.setHeight(popupDTO.getHeight());
        popup.setPosX(popupDTO.getPosX());
        popup.setPosY(popupDTO.getPosY());
        popup.setCloseType(popupDTO.getCloseType());
        popup.setDeviceType(popupDTO.getDeviceType());
        popup.setUseYn(popupDTO.getUseYn());
        // createdAt, deleteYn handled by PrePersist
        popup.setCreatedBy(createdBy);

        popup.setStartDatetime(popupDTO.getStartDatetime());
        popup.setEndDatetime(popupDTO.getEndDatetime());

        popupRepository.save(popup);
    }

    /**
     * 팝업 수정
     */
    @Transactional
    public void updatePopup(PopupDTO popupDTO, Long updatedBy) throws IOException {
        Popup popup = popupRepository.findById(popupDTO.getPopupId())
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));

        // 파일 업로드 처리 (새로운 파일이 있을 경우만)
        if (popupDTO.getImageFile() != null && !popupDTO.getImageFile().isEmpty()) {
            String imagePath = fileStorageService.upload(popupDTO.getImageFile(), "popup");
            popup.setContentImage(imagePath);
        }

        // DTO -> Entity 변환 (Dirty Checking)
        popup.setTitle(popupDTO.getTitle());
        popup.setContentHtml(popupDTO.getContentHtml());
        popup.setLinkType(popupDTO.getLinkType());
        popup.setLinkUrl(popupDTO.getLinkUrl());
        popup.setWidth(popupDTO.getWidth());
        popup.setHeight(popupDTO.getHeight());
        popup.setPosX(popupDTO.getPosX());
        popup.setPosY(popupDTO.getPosY());
        popup.setCloseType(popupDTO.getCloseType());
        popup.setDeviceType(popupDTO.getDeviceType());
        popup.setUseYn(popupDTO.getUseYn());
        popup.setUpdatedBy(updatedBy);

        popup.setStartDatetime(popupDTO.getStartDatetime());
        popup.setEndDatetime(popupDTO.getEndDatetime());

        // popupRepository.save(popup); // Optional
    }

    /**
     * 팝업 삭제
     */
    @Transactional
    public void deletePopup(int popupId, Long deletedBy) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));

        popup.setDeleteYn("Y");
        popup.setDeletedBy(deletedBy);
        // popup.setDeletedAt(LocalDateTime.now());
        popup.setDeletedAt(java.time.LocalDateTime.now());
    }
}

package com.nanum.admin.management.service;

import com.nanum.domain.popup.dto.PopupDTO;
import com.nanum.domain.popup.model.Popup;
import com.nanum.domain.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPopupService {

    private final PopupRepository popupRepository;

    public List<PopupDTO.Response> getPopups() {
        return popupRepository.findAll().stream()
                .map(PopupDTO.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createPopup(PopupDTO.Request request) {
        Popup popup = Popup.builder()
                .title(request.getTitle())
                .contentImage(request.getContentImage())
                .contentHtml(request.getContentHtml())
                .linkUrl(request.getLinkUrl())
                .width(request.getWidth())
                .height(request.getHeight())
                .posX(request.getPosX())
                .posY(request.getPosY())
                .closeType(request.getCloseType())
                .deviceType(request.getDeviceType())
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .useYn(request.getUseYn())
                .deleteYn("N")
                .build();
        popupRepository.save(popup);
        return popup.getId();
    }

    @Transactional
    public void updatePopup(Long id, PopupDTO.Request request) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));
        popup.update(request.getTitle(), request.getContentImage(), request.getLinkUrl(), request.getWidth(),
                request.getHeight(), request.getPosX(), request.getPosY(), request.getCloseType(),
                request.getStartDatetime(), request.getEndDatetime(), request.getUseYn());
    }

    @Transactional
    public void deletePopup(Long id) {
        popupRepository.deleteById(id);
    }
}

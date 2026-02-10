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
    private final com.nanum.domain.file.service.FileService fileService;

    public List<PopupDTO.Response> getPopups() {
        return popupRepository.findAll().stream()
                .map(popup -> {
                    PopupDTO.Response response = PopupDTO.Response.from(popup);
                    List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                            .getFiles(com.nanum.domain.file.model.ReferenceType.POPUP, String.valueOf(popup.getId()))
                            .stream()
                            .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                            .collect(Collectors.toList());
                    response.setFiles(files);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createPopup(PopupDTO.Request request, List<org.springframework.web.multipart.MultipartFile> files) {
        Popup popup = Popup.builder()
                .title(request.getTitle())
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

    @Transactional
    public void updatePopup(Long id, PopupDTO.Request request,
            List<org.springframework.web.multipart.MultipartFile> files) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));
        popup.update(request.getTitle(), request.getLinkUrl(), request.getWidth(),
                request.getHeight(), request.getPosX(), request.getPosY(), request.getCloseType(),
                request.getStartDatetime(), request.getEndDatetime(), request.getUseYn());

        if (files != null && !files.isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                fileService.uploadFile(file, com.nanum.domain.file.model.ReferenceType.POPUP,
                        String.valueOf(popup.getId()), false);
            }
        }
    }

    @Transactional
    public void deletePopup(Long id, String memberCode) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다."));
        popup.delete(memberCode);
        fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.POPUP, String.valueOf(id), memberCode);
    }
}

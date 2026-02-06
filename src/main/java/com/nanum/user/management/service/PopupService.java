package com.nanum.user.management.service;

import com.nanum.domain.popup.dto.PopupDTO;
import com.nanum.domain.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

    private final PopupRepository popupRepository;
    private final com.nanum.domain.file.service.FileService fileService;

    public List<PopupDTO.Response> getPopups() {
        // Ideally filter by UseYn='Y' and Date within range via Repository Query
        return popupRepository.findAll().stream()
                .filter(p -> "N".equals(p.getDeleteYn()))
                .filter(p -> "Y".equals(p.getUseYn()))
                .filter(p -> {
                    LocalDateTime now = LocalDateTime.now();
                    return p.getStartDatetime().isBefore(now) && p.getEndDatetime().isAfter(now);
                })
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
}

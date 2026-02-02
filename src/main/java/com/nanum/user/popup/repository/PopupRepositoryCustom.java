package com.nanum.user.popup.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.popup.model.Popup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PopupRepositoryCustom {
    Page<Popup> searchPopups(SearchDTO searchDTO, Pageable pageable);
}

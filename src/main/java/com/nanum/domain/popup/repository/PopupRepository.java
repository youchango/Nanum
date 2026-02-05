package com.nanum.domain.popup.repository;

import com.nanum.domain.popup.model.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long> {
}

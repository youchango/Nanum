package com.nanum.user.popup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.popup.model.Popup;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Integer>, PopupRepositoryCustom {
}

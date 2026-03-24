package com.nanum.domain.popup.repository;

import com.nanum.domain.popup.model.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long> {
    /** 사이트 코드로 팝업 목록 조회 */
    List<Popup> findBySiteCd(String siteCd);
}

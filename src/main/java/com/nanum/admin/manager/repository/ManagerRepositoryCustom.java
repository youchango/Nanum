package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.global.common.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagerRepositoryCustom {
    Page<Manager> searchManagers(SearchDTO searchDTO, Pageable pageable);
}

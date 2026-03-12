package com.nanum.admin.manager.service;

import com.nanum.admin.manager.dto.ManagerMenuDTO;
import com.nanum.admin.manager.entity.ManagerMenu;
import com.nanum.admin.manager.repository.ManagerMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerMenuService {

    private final ManagerMenuRepository managerMenuRepository;

    @Transactional(readOnly = true)
    public List<ManagerMenuDTO.Info> getAllMenus() {
        // 삭제되지 않은(deleteYn='N') 메뉴만 조회하며, 
        // 최상위 메뉴부터 displayOrder 순으로 정렬하여 계층 구조 생성
        List<ManagerMenu> allMenus = managerMenuRepository.findAll().stream()
                .filter(menu -> "N".equals(menu.getDeleteYn()))
                .sorted(java.util.Comparator.comparing(ManagerMenu::getDisplayOrder, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return allMenus.stream()
                .filter(menu -> menu.getParent() == null)
                .map(ManagerMenuDTO.Info::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ManagerMenuDTO.Info getMenu(Long seq) {
        ManagerMenu menu = managerMenuRepository.findById(seq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
        return ManagerMenuDTO.Info.from(menu);
    }

    @Transactional
    public Long createMenu(ManagerMenuDTO.CreateRequest request) {
        ManagerMenu parent = null;
        if (request.getParentMenuSeq() != null) {
            parent = managerMenuRepository.findById(request.getParentMenuSeq())
                    .orElseThrow(() -> new IllegalArgumentException("상위 메뉴가 존재하지 않습니다."));
        }

        ManagerMenu menu = ManagerMenu.builder()
                .menuName(request.getMenuName())
                .menuUrl(request.getMenuUrl())
                .displayYn(request.getDisplayYn())
                .displayOrder(request.getDisplayOrder())
                .menuParameter(request.getMenuParameter())
                .parent(parent)
                .build();
        return managerMenuRepository.save(menu).getMenuSeq();
    }

    @Transactional
    public void updateMenu(ManagerMenuDTO.UpdateRequest request) {
        ManagerMenu menu = managerMenuRepository.findById(request.getMenuSeq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        ManagerMenu parent = null;
        if (request.getParentMenuSeq() != null) {
            parent = managerMenuRepository.findById(request.getParentMenuSeq())
                    .orElseThrow(() -> new IllegalArgumentException("상위 메뉴가 존재하지 않습니다."));
        }

        menu.update(request.getMenuName(), request.getMenuUrl(), request.getDisplayYn(),
                request.getDisplayOrder(), request.getMenuParameter(), parent);
    }

    @Transactional
    public void deleteMenu(Long seq) {
        ManagerMenu menu = managerMenuRepository.findById(seq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
        
        // BaseEntity의 delete 메소드 사용 (Soft Delete)
        menu.delete("SYSTEM");
    }
}

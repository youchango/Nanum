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
        // Return hierarchy or flat list?
        // Assuming we want top-level menus and their children are fetched lazily or
        // eager?
        // Let's return all top-level menus (parent is null)
        // But Repository needs findByParentIsNull()
        // Or just findAll() and filter in memory for root, constructing hierarchy.

        List<ManagerMenu> allMenus = managerMenuRepository.findAll();
        // Return only root menus, children are inside
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
                .programUrl(request.getProgramUrl())
                .displayYn(request.getDisplayYn())
                .displayOrder(request.getDisplayOrder())
                .programParameter(request.getProgramParameter())
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

        menu.update(request.getMenuName(), request.getProgramUrl(), request.getDisplayYn(),
                request.getDisplayOrder(), request.getProgramParameter(), parent);
    }

    @Transactional
    public void deleteMenu(Long seq) {
        managerMenuRepository.deleteById(seq);
    }
}

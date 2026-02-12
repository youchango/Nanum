package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerMenuDTO;
import com.nanum.admin.manager.service.ManagerMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/manager/menu")
@RequiredArgsConstructor
@Tag(name = "Manager Menu", description = "관리자 메뉴 관리 API")
public class ManagerMenuController {

    private final ManagerMenuService managerMenuService;

    @GetMapping
    @Operation(summary = "메뉴 목록 조회 (계층형)")
    public ResponseEntity<List<ManagerMenuDTO.Info>> getMenus() {
        return ResponseEntity.ok(managerMenuService.getAllMenus());
    }

    @GetMapping("/{seq}")
    @Operation(summary = "메뉴 상세 조회")
    public ResponseEntity<ManagerMenuDTO.Info> getMenu(@PathVariable Long seq) {
        return ResponseEntity.ok(managerMenuService.getMenu(seq));
    }

    @PostMapping
    @Operation(summary = "메뉴 생성")
    public ResponseEntity<Long> createMenu(@RequestBody ManagerMenuDTO.CreateRequest request) {
        return ResponseEntity.ok(managerMenuService.createMenu(request));
    }

    @PutMapping
    @Operation(summary = "메뉴 수정")
    public ResponseEntity<Void> updateMenu(@RequestBody ManagerMenuDTO.UpdateRequest request) {
        managerMenuService.updateMenu(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seq}")
    @Operation(summary = "메뉴 삭제")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long seq) {
        managerMenuService.deleteMenu(seq);
        return ResponseEntity.ok().build();
    }
}

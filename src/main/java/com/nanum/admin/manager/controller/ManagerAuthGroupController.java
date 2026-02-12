package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerAuthGroupDTO;
import com.nanum.admin.manager.service.ManagerAuthGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/manager/auth-group")
@RequiredArgsConstructor
@Tag(name = "Manager Auth Group", description = "관리자 권한 그룹 관리 API")
public class ManagerAuthGroupController {

    private final ManagerAuthGroupService managerAuthGroupService;

    @GetMapping
    @Operation(summary = "권한 그룹 목록 조회")
    public ResponseEntity<List<ManagerAuthGroupDTO.Info>> getAuthGroups() {
        return ResponseEntity.ok(managerAuthGroupService.getAllAuthGroups());
    }

    @GetMapping("/{seq}")
    @Operation(summary = "권한 그룹 상세 조회")
    public ResponseEntity<ManagerAuthGroupDTO.Info> getAuthGroup(@PathVariable Long seq) {
        return ResponseEntity.ok(managerAuthGroupService.getAuthGroup(seq));
    }

    @PostMapping
    @Operation(summary = "권한 그룹 생성")
    public ResponseEntity<Long> createAuthGroup(@RequestBody ManagerAuthGroupDTO.CreateRequest request) {
        return ResponseEntity.ok(managerAuthGroupService.createAuthGroup(request));
    }

    @PutMapping
    @Operation(summary = "권한 그룹 수정")
    public ResponseEntity<Void> updateAuthGroup(@RequestBody ManagerAuthGroupDTO.UpdateRequest request) {
        managerAuthGroupService.updateAuthGroup(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seq}")
    @Operation(summary = "권한 그룹 삭제")
    public ResponseEntity<Void> deleteAuthGroup(@PathVariable Long seq) {
        managerAuthGroupService.deleteAuthGroup(seq);
        return ResponseEntity.ok().build();
    }
}

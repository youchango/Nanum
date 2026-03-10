package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminManager", description = "AdminManager API")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminManagerController {

    private final ManagerService managerService;

    @PostMapping("/managers")
    public ResponseEntity<Void> createManager(@RequestBody ManagerDTO.CreateRequest request) {
        managerService.createManager(request);
        return ResponseEntity.ok().build();
    }

    @org.springframework.web.bind.annotation.GetMapping("/managers")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<java.util.Map<String, Object>>> getManagers(
            @org.springframework.web.bind.annotation.ModelAttribute com.nanum.global.common.dto.SearchDTO searchDTO) {
        org.springframework.data.domain.Page<ManagerDTO.ManagerInfo> managerPage = managerService
                .getManagers(searchDTO);

        java.util.Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("managerList", managerPage.getContent());
        responseData.put("totalCount", managerPage.getTotalElements());

        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(responseData));
    }

    @org.springframework.web.bind.annotation.GetMapping("/managers/{managerCode}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<ManagerDTO.ManagerInfo>> getManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode) {
        return ResponseEntity
                .ok(com.nanum.global.common.dto.ApiResponse.success(managerService.getManager(managerCode)));
    }

    @org.springframework.web.bind.annotation.PutMapping("/managers/{managerCode}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> updateManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode,
            @RequestBody ManagerDTO.CreateRequest request) {
        managerService.updateManager(managerCode, request);
        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(null));
    }

    @PostMapping("/managers/{managerCode}/approve")
    public ResponseEntity<Void> approveManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode) {
        managerService.approveManager(managerCode);
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자를 승인 취소 또는 삭제(비활성화) 처리합니다.
     */
    @org.springframework.web.bind.annotation.DeleteMapping("/managers/{managerCode}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> deleteManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode) {
        managerService.deleteManager(managerCode);
        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(null));
    }

}

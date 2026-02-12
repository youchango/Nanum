package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.service.ManagerService;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ManagerController {

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

    @org.springframework.web.bind.annotation.GetMapping("/managers/{id}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<ManagerDTO.ManagerInfo>> getManager(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(managerService.getManager(id)));
    }

    @org.springframework.web.bind.annotation.PutMapping("/managers/{id}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> updateManager(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @RequestBody ManagerDTO.CreateRequest request) {
        managerService.updateManager(id, request);
        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(null));
    }

    @PostMapping("/managers/{id}/approve")
    public ResponseEntity<Void> approveManager(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        managerService.approveManager(id);
        return ResponseEntity.ok().build();
    }

}

package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.service.ManagerService;
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

    @PostMapping("/auth/login")
    public ResponseEntity<ManagerDTO.LoginResponse> login(@RequestBody ManagerDTO.LoginRequest request) {
        return ResponseEntity.ok(managerService.login(request));
    }

    @PostMapping("/managers")
    public ResponseEntity<Void> createManager(@RequestBody ManagerDTO.CreateRequest request) {
        managerService.createManager(request);
        return ResponseEntity.ok().build();
    }

    @org.springframework.web.bind.annotation.GetMapping("/managers")
    public ResponseEntity<java.util.List<ManagerDTO.ManagerInfo>> getManagers(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String applyYn) {
        return ResponseEntity.ok(managerService.getManagers(applyYn));
    }

    @PostMapping("/managers/{id}/approve")
    public ResponseEntity<Void> approveManager(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        managerService.approveManager(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ManagerDTO.LoginResponse> refresh(jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // If not in cookie, check body or header (optional context)
        // For now, assuming cookie or error
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token missing");
        }

        return ResponseEntity.ok(managerService.refresh(refreshToken));
    }
}

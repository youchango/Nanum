package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.dto.ScmSignupRequest;
import com.nanum.admin.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class ManagerAuthController {

    private final ManagerService managerService;

    @PostMapping("/login")
    public ResponseEntity<ManagerDTO.LoginResponse> login(@RequestBody ManagerDTO.LoginRequest request) {
        return ResponseEntity.ok(managerService.login(request));
    }

    @PostMapping("/refresh")
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

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token missing");
        }

        return ResponseEntity.ok(managerService.refresh(refreshToken));
    }

    @PostMapping(value = "/signup/scm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> signupScm(@ModelAttribute ScmSignupRequest request) {
        managerService.signupScm(request);
        return ResponseEntity.ok().build();
    }
}

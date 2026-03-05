package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.dto.ScmSignupRequest;
import com.nanum.admin.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class ManagerAuthController {

    private final ManagerService managerService;

    @PostMapping("/login")
    public ResponseEntity<ManagerDTO.LoginResponse> login(
            @RequestBody ManagerDTO.LoginRequest request,
            jakarta.servlet.http.HttpServletResponse response) {

        ManagerDTO.LoginResponse loginResponse = managerService.login(request);

        // 액세스 토큰 쿠키 설정
        jakarta.servlet.http.Cookie accessCookie = new jakarta.servlet.http.Cookie("accessToken",
                loginResponse.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600); // 1시간
        // accessCookie.setSecure(true); // HTTPS 환경에서만 전송 시 활성화
        response.addCookie(accessCookie);

        // 리프레시 토큰 쿠키 설정
        jakarta.servlet.http.Cookie refreshCookie = new jakarta.servlet.http.Cookie("refreshToken",
                loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(86400); // 1일
        // refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(loginResponse);
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is missing or invalid");
        }

        return ResponseEntity.ok(managerService.refresh(refreshToken));
    }

    @PostMapping(value = "/signup/scm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> signupScm(@ModelAttribute ScmSignupRequest request) {
        managerService.signupScm(request);
        return ResponseEntity.ok().build();
    }
}

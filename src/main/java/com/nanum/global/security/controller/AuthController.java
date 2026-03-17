package com.nanum.global.security.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nanum.domain.member.model.Member;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements ResponseSupport {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final com.nanum.user.member.service.MemberService memberService;
    // private final com.nanum.global.security.service.AuthService authService; //
    // Removed if not used/exists

    @Operation(summary = "회원가입", description = "신규 회원을 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(
            @jakarta.validation.Valid @RequestBody com.nanum.domain.member.dto.MemberDTO memberDTO) {
        memberService.signup(memberDTO);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", null));
    }

    @Operation(summary = "로그인", description = "ID/PW로 로그인하여 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword()));
        } catch (DisabledException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("승인 대기 중인 계정입니다. 관리자 승인 후 로그인할 수 있습니다."));
        } catch (LockedException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("잠긴 계정입니다. 관리자에게 문의하세요."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("아이디 또는 비밀번호가 올바르지 않습니다."));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인에 실패했습니다."));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Access Token Cookie
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // Set to true in prod (HTTPS)
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600); // 1 hour
        response.addCookie(accessTokenCookie);

        // Refresh Token Cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // Set to true in prod
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400); // 1 day
        response.addCookie(refreshTokenCookie);

        com.nanum.global.security.CustomUserDetails userDetails = (com.nanum.global.security.CustomUserDetails) authentication
                .getPrincipal();
        MemberInfo memberInfo = MemberInfo.from(userDetails.getMember());

        return success(new TokenDto(accessToken, refreshToken, memberInfo));
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token을 이용하여 새로운 Access/Refresh Token을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refresh(jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body(ApiResponse.error("유효하지 않거나 만료된 리프레시 토큰입니다."));
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Access Token Cookie
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // Set to true in prod
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600); // 1 hour
        response.addCookie(accessTokenCookie);

        // Refresh Token Cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // Set to true in prod
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400); // 1 day
        response.addCookie(refreshTokenCookie);

        com.nanum.global.security.CustomUserDetails userDetails = (com.nanum.global.security.CustomUserDetails) authentication
                .getPrincipal();
        MemberInfo memberInfo = MemberInfo.from(userDetails.getMember());

        return ResponseEntity
                .ok(ApiResponse.success("토큰이 갱신되었습니다.", new TokenDto(newAccessToken, newRefreshToken, memberInfo)));
    }

    @Data
    public static class LoginRequest {
        private String loginId;
        private String password;
    }

    @Data
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;
        private MemberInfo memberInfo;

        public TokenDto(String accessToken, String refreshToken, MemberInfo memberInfo) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.memberInfo = memberInfo;
        }
    }

    @Data
    public static class MemberInfo {
        private String memberName;
        private String loginId;
        private String role;

        public static MemberInfo from(Member member) {
            MemberInfo info = new MemberInfo();
            info.setMemberName(member.getMemberName());
            info.setLoginId(member.getMemberId());
            info.setRole(member.getRole().name());
            return info;
        }
    }
}

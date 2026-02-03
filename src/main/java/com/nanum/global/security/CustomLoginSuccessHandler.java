package com.nanum.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 로그???�공 ???�행?�는 ?�들?�입?�다.
 * ?�용?�의 권한(Role)???�인?�여 ?�절???�?�보???�이지�?리다?�렉?�합?�다.
 */
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * ?�증 ?�공 ???�출?�는 메서?�입?�다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authentication ?�증???�용???�보
     * @throws IOException ?�출???�외
     * @throws ServletException ?�블�??�외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // ?�용?�의 권한 목록??Set?�로 변??
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // 권한???�른 리다?�렉??처리
        if (roles.contains("ROLE_MASTER")) {
            // 관리자??관리자 ?�?�보?�로 ?�동
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_BIZ")) {
            // 기업 ?�용?�는 ?��?�??�이지�??�동
            response.sendRedirect("/");
        } else {
            // ?�반 ?�용??�?기�? 권한?� 메인 ?�이지�??�동
            response.sendRedirect("/");
        }
    }
}

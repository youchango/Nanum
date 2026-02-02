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
 * ë¡œê·¸???±ê³µ ???¤í–‰?˜ëŠ” ?¸ë“¤?¬ì…?ˆë‹¤.
 * ?¬ìš©?ì˜ ê¶Œí•œ(Role)???•ì¸?˜ì—¬ ?ì ˆ???€?œë³´???˜ì´ì§€ë¡?ë¦¬ë‹¤?´ë ‰?¸í•©?ˆë‹¤.
 */
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * ?¸ì¦ ?±ê³µ ???¸ì¶œ?˜ëŠ” ë©”ì„œ?œì…?ˆë‹¤.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authentication ?¸ì¦???¬ìš©???•ë³´
     * @throws IOException ?…ì¶œ???ˆì™¸
     * @throws ServletException ?œë¸”ë¦??ˆì™¸
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // ?¬ìš©?ì˜ ê¶Œí•œ ëª©ë¡??Set?¼ë¡œ ë³€??
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // ê¶Œí•œ???°ë¥¸ ë¦¬ë‹¤?´ë ‰??ì²˜ë¦¬
        if (roles.contains("ROLE_MASTER")) {
            // ê´€ë¦¬ì??ê´€ë¦¬ì ?€?œë³´?œë¡œ ?´ë™
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_BIZ")) {
            // ê¸°ì—… ?¬ìš©?ëŠ” ?¤ì?ì¤??˜ì´ì§€ë¡??´ë™
            response.sendRedirect("/");
        } else {
            // ?¼ë°˜ ?¬ìš©??ë°?ê¸°í? ê¶Œí•œ?€ ë©”ì¸ ?˜ì´ì§€ë¡??´ë™
            response.sendRedirect("/");
        }
    }
}

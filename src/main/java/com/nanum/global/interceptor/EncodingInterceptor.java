package com.nanum.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class EncodingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 전역적으로 응답 인코딩을 UTF-8로 설정
        // 특히 POST 요청 등에서 한글 깨짐 방지
        response.setCharacterEncoding("UTF-8");
        // 필요한 경우 Content-Type도 강제할 수 있음 (선택 사항)
        // response.setContentType("text/html; charset=UTF-8");

        return true;
    }
}

package com.nanum.global.config;

import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.global.security.jwt.JwtAuthenticationFilter;
import com.nanum.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.authentication.dao.DaoAuthenticationProvider authenticationProvider(
            com.nanum.global.security.CustomUserDetailsService userDetailsService) {
        org.springframework.security.authentication.dao.DaoAuthenticationProvider authProvider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(configurationSource())) // CORS 설정 적용
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/resources/**", "/WEB-INF/**").permitAll()
                        .requestMatchers("/", "/login", "/admin/login", "/biz/login", "/signup").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll() // Allow Auth API
                        .requestMatchers("/api/v1/files/editor-upload").authenticated() // 에디터 업로드는 모든 회원 허용
                        .requestMatchers("/api/v1/admin/auth/**").permitAll() // Allow Admin Auth API
                        .requestMatchers("/api/v1/members/check-id").permitAll() // Allow ID Check API
                        .requestMatchers("/api/v1/members/reset-password", "/api/v1/members/send-code", "/api/v1/members/verify-code",
                                "/api/v1/members/send-email-code", "/api/v1/members/verify-email-code").permitAll()
                        .requestMatchers("/api/v1/categories/**", "/api/v1/products/**").permitAll() // Allow User
                                                                                                     // Public API
                        .requestMatchers("/api/v1/inquiries/product/**").permitAll() // Allow Product Q&A Public API
                        .requestMatchers("/api/v1/banners/**", "/api/v1/popups/**").permitAll() // Allow Banner & Popup Public API
                        .requestMatchers("/api/v1/contents/**").permitAll() // Allow Notice/Content Public API
                        .requestMatchers("/api/v1/payments/webhook").permitAll() // Allow PG Webhook
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow Swagger
                        .requestMatchers("/admin/**", "/api/v1/admin/**").hasAnyRole(ManagerType.MASTER.name(), ManagerType.ADMIN.name(), ManagerType.SCM.name())
                        .requestMatchers("/biz/**", "/api/v1/biz/**").hasRole("BIZ")
                        .requestMatchers("/user/**", "/api/v1/user/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // [수정] "*" 대신 실제 접속하는 프론트엔드 도메인과 로컬 개발 환경 도메인을 정확히 입력합니다.
        configuration.setAllowedOrigins(Arrays.asList(
                "https://nanum.ttcc.co.kr", // 실제 운영 프론트엔드 주소
                "http://localhost:5173",    // 로컬 Vite 개발 주소
                "http://localhost:3000"     // 혹시 모를 로컬 React 기본 주소
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

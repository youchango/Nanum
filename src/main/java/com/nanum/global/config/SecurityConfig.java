package com.nanum.global.config;

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
                        .requestMatchers("/api/v1/members/reset-password", "/api/v1/members/send-code", "/api/v1/members/verify-code").permitAll()
                        .requestMatchers("/api/v1/categories/**", "/api/v1/products/**").permitAll() // Allow User
                                                                                                     // Public API
                        .requestMatchers("/api/v1/inquiries/product/**").permitAll() // Allow Product Q&A Public API
                        .requestMatchers("/api/v1/banners/**", "/api/v1/popups/**").permitAll() // Allow Banner & Popup Public API
                        .requestMatchers("/api/v1/contents/**").permitAll() // Allow Notice/Content Public API
                        .requestMatchers("/api/v1/payments/webhook").permitAll() // Allow PG Webhook
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow Swagger
                        .requestMatchers("/admin/**", "/api/v1/admin/**").hasAnyRole("MASTER", "ADMIN", "SCM")
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
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 Origin 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP
                                                                                                            // 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용
        configuration.setMaxAge(3600L); // Preflight 요청 캐시 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

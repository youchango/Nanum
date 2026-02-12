package com.nanum.global.config;

import com.nanum.global.security.CustomUserDetails;
import com.nanum.admin.manager.service.CustomManagerDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomManagerDetails) {
                return Optional.of(((CustomManagerDetails) principal).getManager().getManagerCode());
            } else if (principal instanceof CustomUserDetails) {
                return Optional.of(((CustomUserDetails) principal).getMember().getMemberCode());
            } else if (principal instanceof String) {
                return Optional.of((String) principal); // For anonymous or simple string principals
            }

            return Optional.empty();
        };
    }
}

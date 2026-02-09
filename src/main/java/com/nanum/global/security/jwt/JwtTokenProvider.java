package com.nanum.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.admin.manager.service.CustomManagerDetailsService;
import com.nanum.global.security.CustomUserDetailsService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret:defaultSecretKeyShouldBeLongEnoughForHS256AlgorithmdefaultSecretKeyShouldBeLongEnoughForHS256Algorithm}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds:3600}") // 1 hour
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds:86400}") // 1 day
    private long refreshTokenValidityInSeconds;

    private final CustomUserDetailsService userDetailsService;
    private final CustomManagerDetailsService managerDetailsService;
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        if (keyBytes.length < 32) {
            this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        } else {
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenValidityInSeconds);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidityInSeconds);
    }

    private String createToken(Authentication authentication, long validityInSeconds) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String userType = "MEMBER";
        if (authentication.getPrincipal() instanceof CustomManagerDetails) {
            userType = "MANAGER";
        }

        long now = (new Date()).getTime();
        Date validity = new Date(now + validityInSeconds * 1000);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authorities)
                .claim("userType", userType)
                .issuedAt(new Date())
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userType = (String) claims.get("userType");
        UserDetails userDetails;

        if ("MANAGER".equals(userType)) {
            userDetails = managerDetailsService.loadUserByUsername(claims.getSubject());
        } else {
            userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        }

        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        // Try to get from Cookie first (as per Corporate Standard)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Fallback to Header
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

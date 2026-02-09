package com.nanum.admin.manager.service;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.repository.ManagerRepository;
import com.nanum.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final CustomManagerDetailsService managerDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ManagerDTO.LoginResponse login(ManagerDTO.LoginRequest request) {
        CustomManagerDetails userDetails = (CustomManagerDetails) managerDetailsService
                .loadUserByUsername(request.getId());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Create Authentication token manually since we are avoiding
        // AuthenticationManager for simplicity in this specific task context
        // OR better: Update JwtTokenProvider to support Manager and generate token
        // For now, I'll assume JwtTokenProvider needs a small update to accept
        // CustomManagerDetails.

        // However, standard JwtTokenProvider takes Authentication object.
        // We can create a UsernamePasswordAuthenticationToken.

        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return ManagerDTO.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .manager(ManagerDTO.ManagerInfo.from(userDetails.getManager()))
                .build();
    }

    @Transactional
    public ManagerDTO.LoginResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        org.springframework.security.core.Authentication authentication = jwtTokenProvider
                .getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        CustomManagerDetails userDetails = (CustomManagerDetails) authentication.getPrincipal();

        return ManagerDTO.LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .manager(ManagerDTO.ManagerInfo.from(userDetails.getManager()))
                .build();
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void createManager(ManagerDTO.CreateRequest request) {
        if (managerRepository.findByManagerId(request.getId()).isPresent()) {
            throw new IllegalArgumentException("Manager ID already exists");
        }

        Manager manager = Manager.builder()
                .managerId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .managerName(request.getName())
                .managerEmail(request.getEmail())
                .authGroupSeq(request.getAuthGroupSeq() != null ? request.getAuthGroupSeq() : 0)
                .mbType(request.getType() != null ? request.getType() : "ADMIN")
                .description(request.getDescription())
                .useYn("Y")
                .deleteYn("N")
                .registBy("SYSTEM") // or current user
                .updateBy("SYSTEM")
                .loginFailCount(0)
                .build();

        managerRepository.save(manager);
    }
}

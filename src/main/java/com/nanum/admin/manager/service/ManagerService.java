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

    private final com.nanum.domain.shop.repository.ShopInfoRepository shopInfoRepository;

    @Transactional
    public ManagerDTO.LoginResponse login(ManagerDTO.LoginRequest request) {
        CustomManagerDetails userDetails = (CustomManagerDetails) managerDetailsService
                .loadUserByUsername(request.getId());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        Manager manager = userDetails.getManager();
        if ("N".equals(manager.getUseYn()) || "Y".equals(manager.getDeleteYn())) {
            throw new IllegalArgumentException("사용할 수 없는 계정입니다.");
        }
        if ("N".equals(manager.getApplyYn())) {
            throw new IllegalArgumentException("승인 대기 중인 계정입니다. 관리자 승인 후 이용 가능합니다.");
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
    public void createManager(ManagerDTO.CreateRequest request) {
        if (managerRepository.findByManagerId(request.getId()).isPresent()) {
            throw new IllegalArgumentException("Manager ID already exists");
        }

        // Validate Site Code for ADMIN type
        if ("ADMIN".equals(request.getType())) {
            if (request.getSiteCd() == null || request.getSiteCd().isEmpty()) {
                throw new IllegalArgumentException("상점 관리자(ADMIN) 생성 시 사이트 코드는 필수입니다.");
            }
            if (!shopInfoRepository.existsBySiteCd(request.getSiteCd())) {
                throw new IllegalArgumentException("존재하지 않는 사이트 코드입니다: " + request.getSiteCd());
            }
        }

        Manager manager = Manager.builder()
                .managerId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .managerName(request.getName())
                .managerEmail(request.getEmail())
                .authGroupSeq(request.getAuthGroupSeq() != null ? request.getAuthGroupSeq() : 0)
                .mbType(request.getType() != null ? request.getType() : "ADMIN")
                .siteCd(request.getSiteCd())
                .description(request.getDescription())
                .useYn("Y")
                .applyYn("N") // Default to Unapproved
                .deleteYn("N")
                .registBy("SYSTEM") // or current user
                .updateBy("SYSTEM")
                .loginFailCount(0)
                .build();

        managerRepository.save(manager);
    }

    @Transactional
    public void approveManager(Long managerSeq) {
        Manager manager = managerRepository.findById(managerSeq)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        manager.approve();
    }

    public java.util.List<ManagerDTO.ManagerInfo> getManagers(String applyYn) {
        java.util.List<Manager> managers;
        if (applyYn != null && !applyYn.isEmpty()) {
            managers = managerRepository.findAllByApplyYn(applyYn);
        } else {
            managers = managerRepository.findAll();
        }
        return managers.stream()
                .map(ManagerDTO.ManagerInfo::from)
                .collect(java.util.stream.Collectors.toList());
    }
}

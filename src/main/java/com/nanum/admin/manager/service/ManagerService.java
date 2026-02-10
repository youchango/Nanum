package com.nanum.admin.manager.service;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerAuthGroup;
import com.nanum.admin.manager.entity.ManagerScm;
import com.nanum.admin.manager.repository.ManagerAuthGroupRepository;
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

    private final ManagerAuthGroupRepository managerAuthGroupRepository;
    private final com.nanum.admin.manager.repository.ManagerScmRepository managerScmRepository;

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

        ManagerAuthGroup authGroup = null;
        if (request.getAuthGroupSeq() != null) {
            authGroup = managerAuthGroupRepository.findById(request.getAuthGroupSeq())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한 그룹입니다."));
        } else {
            // Optional: Set default group or throw error if mandatory
            throw new IllegalArgumentException("권한 그룹은 필수입니다.");
        }

        Manager manager = Manager.builder()
                .managerId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .managerName(request.getName())
                .managerEmail(request.getEmail())
                .authGroup(authGroup)
                .mbType(request.getType() != null ? request.getType() : "ADMIN")
                .siteCd(request.getSiteCd())
                .description(request.getDescription())
                .useYn("Y")
                .applyYn("N") // Default to Unapproved
                .loginFailCount(0)
                .build();

        if ("SCM".equals(request.getType()) && request.getScmInfo() != null) {
            ManagerScm scm = ManagerScm.builder()
                    .manager(manager)
                    .brandName(request.getScmInfo().getBrandName())
                    .scmCeo(request.getScmInfo().getScmCeo())
                    .scmCorp(request.getScmInfo().getScmCorp())
                    .scmType(request.getScmInfo().getScmType())
                    .scmBsn(request.getScmInfo().getScmBsn())
                    .scmPsn(request.getScmInfo().getScmPsn())
                    .scmUptae(request.getScmInfo().getScmUptae())
                    .scmUpjong(request.getScmInfo().getScmUpjong())
                    .scmZipcode(request.getScmInfo().getScmZipcode())
                    .scmAddr1(request.getScmInfo().getScmAddr1())
                    .scmAddr2(request.getScmInfo().getScmAddr2())
                    .scmPhone(request.getScmInfo().getScmPhone())
                    .scmFax(request.getScmInfo().getScmFax())
                    .scmDamName(request.getScmInfo().getScmDamName())
                    .scmDamPosition(request.getScmInfo().getScmDamPosition())
                    .scmDamPhone(request.getScmInfo().getScmDamPhone())
                    .scmDamEmail(request.getScmInfo().getScmDamEmail())
                    .scmBankName(request.getScmInfo().getScmBankName())
                    .scmBankAccountNum(request.getScmInfo().getScmBankAccountNum())
                    .scmBankAccountName(request.getScmInfo().getScmBankAccountName())
                    .shippingZipcode(request.getScmInfo().getShippingZipcode())
                    .shippingAddr1(request.getScmInfo().getShippingAddr1())
                    .shippingAddr2(request.getScmInfo().getShippingAddr2())
                    .returnZipcode(request.getScmInfo().getReturnZipcode())
                    .returnAddr1(request.getScmInfo().getReturnAddr1())
                    .returnAddr2(request.getScmInfo().getReturnAddr2())
                    .build();

            managerScmRepository.save(scm);
        }

        manager = managerRepository.save(manager);

        if ("SCM".equals(request.getType()) && request.getScmInfo() != null) {
            // Re-build SCM with saved manager just in case, though object reference is
            // enough
            // The code block above creates 'scm' but doesn't persist it.
            // I need to persist it.
            // Since ManagerScmRepository exists, use it?
            // Or add to manager.managerScm and let cascade handle it?
            // If I add to manager, I need manager.setManagerScm(scm).
            // Does Manager have setter for SCM? Lombok @Builder usually doesn't create
            // setters unless @Setter is present.
            // Manager has @Getter but no @Setter on class level?
            // It has @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
            // @Builder
            // So no public setters unless I added them.
            // I did NOT add @Setter to Manager.
            // So I must save ManagerScm explicitly using Repository.

            // I need to inject ManagerScmRepository.
        }
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

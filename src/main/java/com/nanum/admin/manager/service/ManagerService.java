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
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final com.nanum.domain.file.service.FileService fileService;

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

        // Generate Manager Code
        // Policy: ADMIN -> ADM + 6 digits, SCM -> SCM + 6 digits (handled in
        // AdminAuthService but duplicated here for Admin creation or SCM creation by
        // Admin)
        String prefix = "ADMIN".equals(request.getType() != null ? request.getType().name() : "") ? "ADM"
                : ("SCM".equals(request.getType() != null ? request.getType().name() : "") ? "SCM" : "MGR");
        String managerCode = generateManagerCode(prefix);

        Manager manager = Manager.builder()
                .managerCode(managerCode)
                .managerId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .managerName(request.getName())
                .managerEmail(request.getEmail())
                .authGroup(authGroup)
                .mbType(request.getType() != null ? request.getType()
                        : com.nanum.admin.manager.entity.ManagerType.ADMIN)
                .siteCd(request.getSiteCd())
                .description(request.getDescription())
                .useYn("Y")
                .applyYn("N") // Default to Unapproved
                .loginFailCount(0)
                .build();

        if (com.nanum.admin.manager.entity.ManagerType.SCM.equals(request.getType()) && request.getScmInfo() != null) {
            ManagerScm scm = ManagerScm.builder()
                    .manager(manager)
                    .managerCode(managerCode) // Set managerCode
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
    }

    private String generateManagerCode(String prefix) {
        return managerRepository.findTopByManagerCodeStartingWithOrderByManagerCodeDesc(prefix)
                .map(m -> {
                    String lastCode = m.getManagerCode();
                    if (lastCode.length() < prefix.length() + 6)
                        return prefix + "000001";

                    String numberPart = lastCode.substring(prefix.length());
                    try {
                        long number = Long.parseLong(numberPart);
                        return String.format("%s%06d", prefix, number + 1);
                    } catch (NumberFormatException e) {
                        return prefix + "000001";
                    }
                })
                .orElse(prefix + "000001");
    }

    @Transactional
    public void approveManager(Long managerSeq) {
        Manager manager = managerRepository.findById(managerSeq)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        manager.approve();
    }

    @Transactional(readOnly = true)
    public ManagerDTO.ManagerInfo getManager(Long managerSeq) {
        Manager manager = managerRepository.findById(managerSeq)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
        return ManagerDTO.ManagerInfo.from(manager);
    }

    @Transactional
    public void updateManager(Long managerSeq, ManagerDTO.CreateRequest request) {
        Manager manager = managerRepository.findById(managerSeq)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        // Update fields (excluding Password, ID, Code)
        ManagerAuthGroup authGroup = null;
        if (request.getAuthGroupSeq() != null) {
            authGroup = managerAuthGroupRepository.findById(request.getAuthGroupSeq())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한 그룹입니다."));
        }

        manager.updateInfo(
                request.getName(),
                request.getEmail(),
                request.getDescription(),
                request.getSiteCd(),
                request.getType(),
                authGroup);

        // ApplyYn is handled by approveManager, UseYn/DeleteYn by deleteManager (not
        // yet impl)
    }

    public org.springframework.data.domain.Page<ManagerDTO.ManagerInfo> getManagers(
            com.nanum.global.common.dto.SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getRecordSize());
        return managerRepository.searchManagers(searchDTO, pageable)
                .map(ManagerDTO.ManagerInfo::from);
    }

    @Transactional
    public void signupScm(com.nanum.admin.manager.dto.ScmSignupRequest request) {
        // 1. Check ID Duplicate
        if (managerRepository.findByManagerId(request.getManagerId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. Generate Manager Code
        String managerCode = generateManagerCode("SCM");

        // 3. Set Audit Context (Temporary)
        setTemporaryAuditContext(managerCode);

        try {
            // 4. File Upload (Business License)
            if (request.getBusinessLicense() != null && !request.getBusinessLicense().isEmpty()) {
                fileService.uploadFile(request.getBusinessLicense(), com.nanum.domain.file.model.ReferenceType.SCM,
                        managerCode, true);
            }

            // 5. Create Manager
            // Find default SCM Auth Group (fallback to ID 1)
            ManagerAuthGroup authGroup = managerAuthGroupRepository.findAll().stream()
                    .filter(g -> "SCM".equals(g.getAuthGroupName()))
                    .findFirst()
                    .orElse(managerAuthGroupRepository.findById(1L)
                            .orElseThrow(() -> new IllegalArgumentException("기본 권한 그룹을 찾을 수 없습니다.")));

            Manager manager = Manager.builder()
                    .managerCode(managerCode)
                    .managerId(request.getManagerId())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .managerName(request.getManagerName())
                    .managerEmail(request.getManagerEmail())
                    .authGroup(authGroup)
                    .mbType(com.nanum.admin.manager.entity.ManagerType.SCM)
                    .useYn("Y")
                    .applyYn("N")
                    .loginFailCount(0)
                    .build();

            managerRepository.save(manager);

            // 6. Create ManagerScm
            ManagerScm scm = ManagerScm.builder()
                    .manager(manager)
                    .managerCode(managerCode)
                    .brandName(request.getBrandName())
                    .scmCeo(request.getScmCeo())
                    .scmCorp(request.getScmCorp())
                    .scmType(request.getScmType())
                    .scmBsn(request.getScmBsn())
                    .scmPsn(request.getScmPsn())
                    .scmUptae(request.getScmUptae())
                    .scmUpjong(request.getScmUpjong())
                    .scmZipcode(request.getScmZipcode())
                    .scmAddr1(request.getScmAddr1())
                    .scmAddr2(request.getScmAddr2())
                    .scmPhone(request.getScmPhone())
                    .scmFax(request.getScmFax())
                    .scmDamName(request.getScmDamName())
                    .scmDamPosition(request.getScmDamPosition())
                    .scmDamPhone(request.getScmDamPhone())
                    .scmDamEmail(request.getScmDamEmail())
                    .scmBankName(request.getScmBankName())
                    .scmBankAccountNum(request.getScmBankAccountNum())
                    .scmBankAccountName(request.getScmBankAccountName())
                    .shippingZipcode(request.getShippingZipcode())
                    .shippingAddr1(request.getShippingAddr1())
                    .shippingAddr2(request.getShippingAddr2())
                    .returnZipcode(request.getReturnZipcode())
                    .returnAddr1(request.getReturnAddr1())
                    .returnAddr2(request.getReturnAddr2())
                    .build();

            managerScmRepository.save(scm);
        } finally {
            // 7. Clear Context
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private void setTemporaryAuditContext(String managerCode) {
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                managerCode, null, java.util.Collections.emptyList());
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
    }
}

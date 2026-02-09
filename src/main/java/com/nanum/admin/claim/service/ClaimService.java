package com.nanum.admin.claim.service;

import com.nanum.admin.claim.entity.Claim;
import com.nanum.admin.claim.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaimService {

    private final ClaimRepository claimRepository;

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim getClaim(@lombok.NonNull Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimId));
    }

    @Transactional
    public Claim updateClaimStatus(Long claimId, String status, String managerCode) {
        Claim claim = getClaim(claimId);
        claim.setClaimStatus(status);
        claim.setClaimCheckCd(managerCode);
        claim.setClaimDateCheck(java.time.LocalDateTime.now());
        return claim;
    }
}

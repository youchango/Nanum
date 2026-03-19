package com.nanum.admin.site.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.domain.shop.repository.ShopInfoRepository;
import com.nanum.admin.site.dto.SitePolicyDTO;
import com.nanum.admin.site.repository.SitePolicyHistoryRepository;
import com.nanum.domain.site.model.SitePolicyHistory;
import com.nanum.domain.shop.model.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SitePolicyService {

    private final SitePolicyHistoryRepository sitePolicyHistoryRepository;
    private final ShopInfoRepository shopInfoRepository;

    @Transactional(readOnly = true)
    public SitePolicyDTO getPolicyBySiteCd(String siteCd) {
        return sitePolicyHistoryRepository.findTopBySiteCdOrderByCreatedAtDesc(siteCd)
                .map(SitePolicyDTO::fromForAdmin)
                .orElse(SitePolicyDTO.builder().siteCd(siteCd).build()); // Return empty DTO if not found
    }

    @Transactional
    public void savePolicy(String siteCd, SitePolicyDTO dto, Manager manager) {
        // Validate permissions (Double-check, though Controller should intercept)
        if (manager.getMbType() == ManagerType.ADMIN && !siteCd.equals(manager.getSiteCd())) {
            throw new IllegalArgumentException("본인의 사이트 정책만 수정할 수 있습니다.");
        }

        SitePolicyHistory history = SitePolicyHistory.builder()
                .siteCd(siteCd)
                .termsOfUse(dto.getTermsOfUse())
                .privacyPolicy(dto.getPrivacyPolicy())
                .legalNotice(dto.getLegalNotice())
                .footerInfo(dto.getFooterInfo())
                .build();

        // Audit will handle createdBy/updatedBy automatically if configured, otherwise
        // manual set
        // history.setCreatedBy(manager.getManagerSeq()); // If manual

        sitePolicyHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<ShopInfo> getAllSites() {
        return shopInfoRepository.findAll();
    }
}

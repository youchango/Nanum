package com.nanum.admin.site.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.domain.shop.repository.ShopInfoRepository;
import com.nanum.admin.site.dto.SitePolicyDTO;
import com.nanum.admin.site.repository.SitePolicyRepository;
import com.nanum.domain.site.model.SitePolicy;
import com.nanum.domain.shop.model.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SitePolicyService {

    private final SitePolicyRepository sitePolicyRepository;
    private final ShopInfoRepository shopInfoRepository;

    @Transactional(readOnly = true)
    public SitePolicyDTO getPolicyBySiteCd(String siteCd) {
        return sitePolicyRepository.findBySiteCd(siteCd)
                .map(SitePolicyDTO::fromForAdmin)
                .orElse(SitePolicyDTO.builder().siteCd(siteCd).build()); // Return empty DTO if not found
    }

    @Transactional
    public void savePolicy(String siteCd, SitePolicyDTO dto, Manager manager) {
        // Validate permissions
        if (manager.getMbType() == ManagerType.ADMIN && !siteCd.equals(manager.getSiteCd())) {
            throw new IllegalArgumentException("본인의 사이트 정책만 수정할 수 있습니다.");
        }

        SitePolicy policy = sitePolicyRepository.findBySiteCd(siteCd)
                .orElseGet(() -> SitePolicy.builder().siteCd(siteCd).build());

        policy.updatePolicy(
                dto.getTermsOfUse(),
                dto.getPrivacyPolicy(),
                dto.getLegalNotice(),
                dto.getMarketingConsent(),
                dto.getFooterInfo()
        );

        sitePolicyRepository.save(policy);
    }

    @Transactional(readOnly = true)
    public List<ShopInfo> getAllSites() {
        return shopInfoRepository.findAll();
    }
}

package com.nanum.admin.site.dto;

import com.nanum.domain.site.model.SitePolicy;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SitePolicyDTO {
    private String siteCd;
    private String termsOfUse;
    private String privacyPolicy;
    private String legalNotice;
    private String marketingConsent;
    private String footerInfo;

    public static SitePolicyDTO fromForAdmin(SitePolicy entity) {
        if (entity == null)
            return null;
        return SitePolicyDTO.builder()
                .siteCd(entity.getSiteCd())
                .termsOfUse(entity.getTermsOfUse())
                .privacyPolicy(entity.getPrivacyPolicy())
                .legalNotice(entity.getLegalNotice())
                .marketingConsent(entity.getMarketingConsent())
                .footerInfo(entity.getFooterInfo())
                .build();
    }
}

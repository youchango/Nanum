package com.nanum.domain.site.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "site_policy_history")
public class SitePolicyHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "site_cd", length = 20, nullable = false)
    private String siteCd;

    @Lob
    @Column(name = "terms_of_use", columnDefinition = "MEDIUMTEXT")
    private String termsOfUse;

    @Lob
    @Column(name = "privacy_policy", columnDefinition = "MEDIUMTEXT")
    private String privacyPolicy;

    @Lob
    @Column(name = "legal_notice", columnDefinition = "MEDIUMTEXT")
    private String legalNotice;

    @Lob
    @Column(name = "footer_info", columnDefinition = "TEXT")
    private String footerInfo;

    @Builder
    public SitePolicyHistory(String siteCd, String termsOfUse, String privacyPolicy, String legalNotice,
            String footerInfo) {
        this.siteCd = siteCd;
        this.termsOfUse = termsOfUse;
        this.privacyPolicy = privacyPolicy;
        this.legalNotice = legalNotice;
        this.footerInfo = footerInfo;
    }
}

package com.nanum.domain.site.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 사이트 정책 엔티티 (UPDATE 기반 단일 정보 관리)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "site_policy")
public class SitePolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "site_cd", length = 20, nullable = false, unique = true)
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
    @Column(name = "marketing_consent", columnDefinition = "MEDIUMTEXT")
    private String marketingConsent;

    @Lob
    @Column(name = "footer_info", columnDefinition = "TEXT")
    private String footerInfo;

    @Builder
    public SitePolicy(String siteCd, String termsOfUse, String privacyPolicy, String legalNotice,
                      String marketingConsent, String footerInfo) {
        this.siteCd = siteCd;
        this.termsOfUse = termsOfUse;
        this.privacyPolicy = privacyPolicy;
        this.legalNotice = legalNotice;
        this.marketingConsent = marketingConsent;
        this.footerInfo = footerInfo;
    }

    /**
     * 정책 정보 갱신 (UPDATE)
     * @param termsOfUse 이용약관
     * @param privacyPolicy 개인정보처리방침
     * @param legalNotice 법적고지
     * @param marketingConsent 마케팅활용동의
     * @param footerInfo 푸터정보
     */
    public void updatePolicy(String termsOfUse, String privacyPolicy, String legalNotice, String marketingConsent, String footerInfo) {
        this.termsOfUse = termsOfUse;
        this.privacyPolicy = privacyPolicy;
        this.legalNotice = legalNotice;
        this.marketingConsent = marketingConsent;
        this.footerInfo = footerInfo;
    }
}

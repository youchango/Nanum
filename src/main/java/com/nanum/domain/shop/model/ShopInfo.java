package com.nanum.domain.shop.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_key")
    private Long shopKey;

    @Column(name = "site_cd", nullable = false, length = 20)
    private String siteCd;

    @Column(name = "shop_type", nullable = false, length = 10)
    private String shopType;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "shop_domain", length = 100)
    private String shopDomain;

    @Column(name = "shop_status", nullable = false, length = 10)
    @ColumnDefault("'R'")
    private String shopStatus; // R:준비, O:운영, S:중지

    @Column(name = "shop_mode", nullable = false, length = 10)
    @ColumnDefault("'개발'")
    private String shopMode;

    @Column(name = "shop_corp", nullable = false, length = 100)
    private String shopCorp;

    @Column(name = "shop_bsn", nullable = false, length = 15)
    private String shopBsn;

    @Column(name = "shop_psn", length = 30)
    private String shopPsn;

    @Column(name = "shop_uptae", length = 50)
    private String shopUptae;

    @Column(name = "shop_upjong", length = 50)
    private String shopUpjong;

    @Column(name = "shop_zipcode", length = 7)
    private String shopZipcode;

    @Column(name = "shop_addr1", length = 100)
    private String shopAddr1;

    @Column(name = "shop_addr2", length = 100)
    private String shopAddr2;

    @Column(name = "shop_phone", length = 20)
    private String shopPhone;

    @Column(name = "shop_fax", length = 20)
    private String shopFax;

    @Column(name = "shop_dam_name", length = 20)
    private String shopDamName;

    @Column(name = "shop_dam_position", length = 20)
    private String shopDamPosition;

    @Column(name = "shop_dam_phone", length = 20)
    private String shopDamPhone;

    @Column(name = "shop_dam_email", length = 50)
    private String shopDamEmail;

    @Column(name = "shop_bank_account_name", length = 20)
    private String shopBankAccountName;

    @Column(name = "shop_bank_name", length = 20)
    private String shopBankName;

    @Column(name = "shop_bank_account_num", length = 50)
    private String shopBankAccountNum;

    @Column(name = "shop_set_product_use_max_point", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal shopSetProductUseMaxPoint;

    @Column(name = "shop_set_product_acc_point", precision = 19, scale = 4)
    @ColumnDefault("0")
    private BigDecimal shopSetProductAccPoint;

    @Column(name = "shop_insert_date", nullable = false)
    private LocalDateTime shopInsertDate;

    @PrePersist
    public void prePersist() {
        this.shopInsertDate = LocalDateTime.now();
    }

    public void update(String siteCd, String shopType, String shopName, String shopDomain, String shopStatus,
            String shopMode, String shopCorp, String shopBsn, String shopPsn, String shopUptae,
            String shopUpjong, String shopZipcode, String shopAddr1, String shopAddr2,
            String shopPhone, String shopFax, String shopDamName, String shopDamPosition,
            String shopDamPhone, String shopDamEmail, String shopBankAccountName, String shopBankName,
            String shopBankAccountNum, BigDecimal shopSetProductUseMaxPoint, BigDecimal shopSetProductAccPoint) {
        this.siteCd = siteCd;
        this.shopType = shopType;
        this.shopName = shopName;
        this.shopDomain = shopDomain;
        this.shopStatus = shopStatus;
        this.shopMode = shopMode;
        this.shopCorp = shopCorp;
        this.shopBsn = shopBsn;
        this.shopPsn = shopPsn;
        this.shopUptae = shopUptae;
        this.shopUpjong = shopUpjong;
        this.shopZipcode = shopZipcode;
        this.shopAddr1 = shopAddr1;
        this.shopAddr2 = shopAddr2;
        this.shopPhone = shopPhone;
        this.shopFax = shopFax;
        this.shopDamName = shopDamName;
        this.shopDamPosition = shopDamPosition;
        this.shopDamPhone = shopDamPhone;
        this.shopDamEmail = shopDamEmail;
        this.shopBankAccountName = shopBankAccountName;
        this.shopBankName = shopBankName;
        this.shopBankAccountNum = shopBankAccountNum;
        this.shopSetProductUseMaxPoint = shopSetProductUseMaxPoint;
        this.shopSetProductAccPoint = shopSetProductAccPoint;
    }
}

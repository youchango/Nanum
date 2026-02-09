package com.nanum.admin.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ShopInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_key")
    private Integer shopKey;

    @Column(name = "site_cd", length = 10, nullable = false)
    private String siteCd;

    @Column(name = "shop_type", length = 10, nullable = false)
    private String shopType;

    @Column(name = "shop_name", length = 100, nullable = false)
    private String shopName;

    @Column(name = "shop_domain", length = 100)
    private String shopDomain;

    @Column(name = "shop_status", length = 10, nullable = false)
    @ColumnDefault("'R'")
    private String shopStatus;

    @Column(name = "shop_mode", length = 2, nullable = false)
    @ColumnDefault("'C'")
    private String shopMode;

    @Column(name = "shop_corp", length = 100, nullable = false)
    private String shopCorp;

    @Column(name = "shop_bsn", length = 15, nullable = false)
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

    @Column(name = "shop_insert_date", nullable = false, updatable = false)
    private LocalDateTime shopInsertDate;

    @PrePersist
    public void prePersist() {
        this.shopInsertDate = LocalDateTime.now();
    }
}

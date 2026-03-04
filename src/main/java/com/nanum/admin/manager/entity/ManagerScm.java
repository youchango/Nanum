package com.nanum.admin.manager.entity;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "manager_scm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ManagerScm extends BaseEntity {

    @Id
    @Column(name = "manager_seq")
    private Long managerSeq;

    @Column(name = "manager_code", length = 20, nullable = false)
    private String managerCode;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "manager_seq")
    private Manager manager;

    @Column(name = "brand_name", length = 50, nullable = false)
    private String brandName;

    @Column(name = "scm_ceo", length = 50, nullable = false)
    private String scmCeo;

    @Column(name = "scm_corp", length = 100, nullable = false)
    private String scmCorp;

    @Column(name = "scm_type", length = 10, nullable = false)
    @ColumnDefault("'CORP'")
    private String scmType; // CORP, INDIV

    @Column(name = "scm_bsn", length = 15, nullable = false)
    private String scmBsn; // Business Number

    @Column(name = "scm_psn", length = 30)
    private String scmPsn; // Online Sales Number

    @Column(name = "scm_uptae", length = 50)
    private String scmUptae;

    @Column(name = "scm_upjong", length = 50)
    private String scmUpjong;

    @Column(name = "scm_zipcode", length = 7)
    private String scmZipcode;

    @Column(name = "scm_addr1", length = 100)
    private String scmAddr1;

    @Column(name = "scm_addr2", length = 100)
    private String scmAddr2;

    @Column(name = "scm_phone", length = 20)
    private String scmPhone;

    @Column(name = "scm_fax", length = 20)
    private String scmFax;

    @Column(name = "scm_dam_name", length = 20)
    private String scmDamName;

    @Column(name = "scm_dam_position", length = 20)
    private String scmDamPosition;

    @Column(name = "scm_dam_phone", length = 20)
    private String scmDamPhone;

    @Column(name = "scm_dam_email", length = 50)
    private String scmDamEmail;

    @Column(name = "scm_bank_name", length = 50, nullable = false)
    private String scmBankName;

    @Column(name = "scm_bank_account_num", length = 200, nullable = false)
    private String scmBankAccountNum; // Encrypted? Logic needed if so. For now string.

    @Column(name = "scm_bank_account_name", length = 50, nullable = false)
    private String scmBankAccountName;

    @Column(name = "shipping_zipcode", length = 10)
    private String shippingZipcode;

    @Column(name = "shipping_addr1", length = 200)
    private String shippingAddr1;

    @Column(name = "shipping_addr2", length = 200)
    private String shippingAddr2;

    @Column(name = "return_zipcode", length = 10)
    private String returnZipcode;

    @Column(name = "return_addr1", length = 200)
    private String returnAddr1;

    @Column(name = "return_addr2", length = 200)
    private String returnAddr2;

    public void update(String brandName, String scmCeo, String scmCorp, String scmType, String scmBsn, String scmPsn,
            String scmUptae, String scmUpjong, String scmZipcode, String scmAddr1, String scmAddr2,
            String scmPhone, String scmFax, String scmDamName, String scmDamPosition, String scmDamPhone,
            String scmDamEmail, String scmBankName, String scmBankAccountNum, String scmBankAccountName,
            String shippingZipcode, String shippingAddr1, String shippingAddr2,
            String returnZipcode, String returnAddr1, String returnAddr2) {
        this.brandName = brandName;
        this.scmCeo = scmCeo;
        this.scmCorp = scmCorp;
        this.scmType = scmType;
        this.scmBsn = scmBsn;
        this.scmPsn = scmPsn;
        this.scmUptae = scmUptae;
        this.scmUpjong = scmUpjong;
        this.scmZipcode = scmZipcode;
        this.scmAddr1 = scmAddr1;
        this.scmAddr2 = scmAddr2;
        this.scmPhone = scmPhone;
        this.scmFax = scmFax;
        this.scmDamName = scmDamName;
        this.scmDamPosition = scmDamPosition;
        this.scmDamPhone = scmDamPhone;
        this.scmDamEmail = scmDamEmail;
        this.scmBankName = scmBankName;
        this.scmBankAccountNum = scmBankAccountNum;
        this.scmBankAccountName = scmBankAccountName;
        this.shippingZipcode = shippingZipcode;
        this.shippingAddr1 = shippingAddr1;
        this.shippingAddr2 = shippingAddr2;
        this.returnZipcode = returnZipcode;
        this.returnAddr1 = returnAddr1;
        this.returnAddr2 = returnAddr2;
    }
}

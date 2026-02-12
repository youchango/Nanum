package com.nanum.admin.manager.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScmSignupRequest {

    // Manager Basic Info
    private String managerId;
    private String password;
    private String managerName;
    private String managerEmail;

    // SCM Info
    private String brandName;
    private String scmCeo;
    private String scmCorp;

    @Builder.Default
    private String scmType = "CORP"; // CORP, INDIV

    private String scmBsn; // Business Number
    private String scmPsn; // Online Sales Number
    private String scmUptae;
    private String scmUpjong;

    private String scmZipcode;
    private String scmAddr1;
    private String scmAddr2;

    private String scmPhone;
    private String scmFax;

    // Manager(Staff) Info
    private String scmDamName;
    private String scmDamPosition;
    private String scmDamPhone;
    private String scmDamEmail;

    // Bank Info
    private String scmBankName;
    private String scmBankAccountNum;
    private String scmBankAccountName;

    // Shipping/Return Address
    private String shippingZipcode;
    private String shippingAddr1;
    private String shippingAddr2;

    private String returnZipcode;
    private String returnAddr1;
    private String returnAddr2;

    // File
    private MultipartFile businessLicense;
}

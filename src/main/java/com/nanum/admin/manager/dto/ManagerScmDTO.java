package com.nanum.admin.manager.dto;

import com.nanum.admin.manager.entity.ManagerScm;
import lombok.*;

public class ManagerScmDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Info {
        private String managerCode;
        private String supplierName;
        private String scmCeo;
        private String scmCorp;
        private String scmType;
        private String scmBsn;
        private String scmPsn;
        private String scmUptae;
        private String scmUpjong;
        private String scmZipcode;
        private String scmAddr1;
        private String scmAddr2;
        private String scmPhone;
        private String scmFax;
        private String scmDamName;
        private String scmDamPosition;
        private String scmDamPhone;
        private String scmDamEmail;
        private String scmBankName;
        private String scmBankAccountNum;
        private String scmBankAccountName;
        private String businessLicenseUrl;

        public static Info from(ManagerScm entity) {
            return Info.builder()
                    .managerCode(entity.getManagerCode())
                    .supplierName(entity.getSupplierName())
                    .scmCeo(entity.getScmCeo())
                    .scmCorp(entity.getScmCorp())
                    .scmType(entity.getScmType())
                    .scmBsn(entity.getScmBsn())
                    .scmPsn(entity.getScmPsn())
                    .scmUptae(entity.getScmUptae())
                    .scmUpjong(entity.getScmUpjong())
                    .scmZipcode(entity.getScmZipcode())
                    .scmAddr1(entity.getScmAddr1())
                    .scmAddr2(entity.getScmAddr2())
                    .scmPhone(entity.getScmPhone())
                    .scmFax(entity.getScmFax())
                    .scmDamName(entity.getScmDamName())
                    .scmDamPosition(entity.getScmDamPosition())
                    .scmDamPhone(entity.getScmDamPhone())
                    .scmDamEmail(entity.getScmDamEmail())
                    .scmBankName(entity.getScmBankName())
                    .scmBankAccountNum(entity.getScmBankAccountNum())
                    .scmBankAccountName(entity.getScmBankAccountName())
                    .build();
        }
    }
}

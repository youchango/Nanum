package com.nanum.domain.shop.dto;

import com.nanum.domain.shop.model.ShopInfo;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShopDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String siteCd;
        private String shopType;
        private String shopName;
        private String shopDomain;
        private String shopStatus;
        private String shopMode;
        private String shopCorp;
        private String shopBsn;
        private String shopPsn;
        private String shopUptae;
        private String shopUpjong;
        private String shopZipcode;
        private String shopAddr1;
        private String shopAddr2;
        private String shopPhone;
        private String shopFax;
        private String shopDamName;
        private String shopDamPosition;
        private String shopDamPhone;
        private String shopDamEmail;
        private String shopBankAccountName;
        private String shopBankName;
        private String shopBankAccountNum;
        private BigDecimal shopSetProductUseMaxPoint;
        private BigDecimal shopSetProductAccPoint;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long shopKey;
        private String siteCd;
        private String shopType;
        private String shopName;
        private String shopDomain;
        private String shopStatus;
        private String shopMode;
        private String shopCorp;
        private String shopBsn;
        private String shopPsn;
        private String shopUptae;
        private String shopUpjong;
        private String shopZipcode;
        private String shopAddr1;
        private String shopAddr2;
        private String shopPhone;
        private String shopFax;
        private String shopDamName;
        private String shopDamPosition;
        private String shopDamPhone;
        private String shopDamEmail;
        private String shopBankAccountName;
        private String shopBankName;
        private String shopBankAccountNum;
        private BigDecimal shopSetProductUseMaxPoint;
        private BigDecimal shopSetProductAccPoint;
        private LocalDateTime shopInsertDate;

        public static Response from(ShopInfo shop) {
            return Response.builder()
                    .shopKey(shop.getShopKey())
                    .siteCd(shop.getSiteCd())
                    .shopType(shop.getShopType())
                    .shopName(shop.getShopName())
                    .shopDomain(shop.getShopDomain())
                    .shopStatus(shop.getShopStatus())
                    .shopMode(shop.getShopMode())
                    .shopCorp(shop.getShopCorp())
                    .shopBsn(shop.getShopBsn())
                    .shopPsn(shop.getShopPsn())
                    .shopUptae(shop.getShopUptae())
                    .shopUpjong(shop.getShopUpjong())
                    .shopZipcode(shop.getShopZipcode())
                    .shopAddr1(shop.getShopAddr1())
                    .shopAddr2(shop.getShopAddr2())
                    .shopPhone(shop.getShopPhone())
                    .shopFax(shop.getShopFax())
                    .shopDamName(shop.getShopDamName())
                    .shopDamPosition(shop.getShopDamPosition())
                    .shopDamPhone(shop.getShopDamPhone())
                    .shopDamEmail(shop.getShopDamEmail())
                    .shopBankAccountName(shop.getShopBankAccountName())
                    .shopBankName(shop.getShopBankName())
                    .shopBankAccountNum(shop.getShopBankAccountNum())
                    .shopSetProductUseMaxPoint(shop.getShopSetProductUseMaxPoint())
                    .shopSetProductAccPoint(shop.getShopSetProductAccPoint())
                    .shopInsertDate(shop.getShopInsertDate())
                    .build();
        }
    }
}

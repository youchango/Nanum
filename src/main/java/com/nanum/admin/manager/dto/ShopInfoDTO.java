package com.nanum.admin.manager.dto;

import com.nanum.domain.shop.model.ShopInfo;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopInfoDTO {
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
    private BigDecimal shopSetProductUseMaxPoint;
    private BigDecimal shopSetProductAccPoint;

    public static ShopInfoDTO from(ShopInfo shopInfo) {
        return ShopInfoDTO.builder()
                .shopKey(shopInfo.getShopKey())
                .siteCd(shopInfo.getSiteCd())
                .shopType(shopInfo.getShopType())
                .shopName(shopInfo.getShopName())
                .shopDomain(shopInfo.getShopDomain())
                .shopStatus(shopInfo.getShopStatus())
                .shopMode(shopInfo.getShopMode())
                .shopCorp(shopInfo.getShopCorp())
                .shopBsn(shopInfo.getShopBsn())
                .shopPsn(shopInfo.getShopPsn())
                .shopUptae(shopInfo.getShopUptae())
                .shopUpjong(shopInfo.getShopUpjong())
                .shopZipcode(shopInfo.getShopZipcode())
                .shopAddr1(shopInfo.getShopAddr1())
                .shopAddr2(shopInfo.getShopAddr2())
                .shopPhone(shopInfo.getShopPhone())
                .shopFax(shopInfo.getShopFax())
                .shopDamName(shopInfo.getShopDamName())
                .shopDamPosition(shopInfo.getShopDamPosition())
                .shopDamPhone(shopInfo.getShopDamPhone())
                .shopDamEmail(shopInfo.getShopDamEmail())
                .shopSetProductUseMaxPoint(shopInfo.getShopSetProductUseMaxPoint())
                .shopSetProductAccPoint(shopInfo.getShopSetProductAccPoint())
                .build();
    }
}

package com.nanum.admin.shop.service;

import com.nanum.domain.shop.dto.ShopDTO;
import com.nanum.domain.shop.model.ShopInfo;
import com.nanum.domain.shop.repository.ShopInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminShopService {

    private final ShopInfoRepository shopInfoRepository;

    public List<ShopDTO.Response> getAllShops() {
        return shopInfoRepository.findAll().stream()
                .map(ShopDTO.Response::from)
                .collect(Collectors.toList());
    }

    public ShopDTO.Response getShop(Long shopKey) {
        ShopInfo shop = shopInfoRepository.findById(shopKey)
                .orElseThrow(() -> new IllegalArgumentException("상점 정보를 찾을 수 없습니다. ID: " + shopKey));
        return ShopDTO.Response.from(shop);
    }

    @Transactional
    public Long createShop(ShopDTO.Request request) {
        if (shopInfoRepository.existsBySiteCd(request.getSiteCd())) {
            throw new IllegalArgumentException("이미 존재하는 사이트 코드입니다: " + request.getSiteCd());
        }

        ShopInfo shop = ShopInfo.builder()
                .siteCd(request.getSiteCd())
                .shopType(request.getShopType())
                .shopName(request.getShopName())
                .shopDomain(request.getShopDomain())
                .shopStatus(request.getShopStatus())
                .shopMode(request.getShopMode())
                .shopCorp(request.getShopCorp())
                .shopBsn(request.getShopBsn())
                .shopPsn(request.getShopPsn())
                .shopUptae(request.getShopUptae())
                .shopUpjong(request.getShopUpjong())
                .shopZipcode(request.getShopZipcode())
                .shopAddr1(request.getShopAddr1())
                .shopAddr2(request.getShopAddr2())
                .shopPhone(request.getShopPhone())
                .shopFax(request.getShopFax())
                .shopDamName(request.getShopDamName())
                .shopDamPosition(request.getShopDamPosition())
                .shopDamPhone(request.getShopDamPhone())
                .shopDamEmail(request.getShopDamEmail())
                .shopBankAccountName(request.getShopBankAccountName())
                .shopBankName(request.getShopBankName())
                .shopBankAccountNum(request.getShopBankAccountNum())
                .shopSetProductUseMaxPoint(request.getShopSetProductUseMaxPoint())
                .shopSetProductAccPoint(request.getShopSetProductAccPoint())
                .build();

        shopInfoRepository.save(shop);
        return shop.getShopKey();
    }

    @Transactional
    public void updateShop(Long shopKey, ShopDTO.Request request) {
        ShopInfo shop = shopInfoRepository.findById(shopKey)
                .orElseThrow(() -> new IllegalArgumentException("상점 정보를 찾을 수 없습니다."));

        shop.update(
                request.getSiteCd(),
                request.getShopType(),
                request.getShopName(),
                request.getShopDomain(),
                request.getShopStatus(),
                request.getShopMode(),
                request.getShopCorp(),
                request.getShopBsn(),
                request.getShopPsn(),
                request.getShopUptae(),
                request.getShopUpjong(),
                request.getShopZipcode(),
                request.getShopAddr1(),
                request.getShopAddr2(),
                request.getShopPhone(),
                request.getShopFax(),
                request.getShopDamName(),
                request.getShopDamPosition(),
                request.getShopDamPhone(),
                request.getShopDamEmail(),
                request.getShopBankAccountName(),
                request.getShopBankName(),
                request.getShopBankAccountNum(),
                request.getShopSetProductUseMaxPoint(),
                request.getShopSetProductAccPoint());
    }

    @Transactional
    public void deleteShop(Long shopKey) {
        ShopInfo shop = shopInfoRepository.findById(shopKey)
                .orElseThrow(() -> new IllegalArgumentException("상점 정보를 찾을 수 없습니다."));
        shopInfoRepository.delete(shop);
    }
}

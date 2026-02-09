package com.nanum.admin.shop.controller;

import com.nanum.admin.shop.service.AdminShopService;
import com.nanum.domain.shop.dto.ShopDTO;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/shops")
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    @GetMapping
    public ApiResponse<List<ShopDTO.Response>> getAllShops() {
        return ApiResponse.success(adminShopService.getAllShops());
    }

    @GetMapping("/{shopKey}")
    public ApiResponse<ShopDTO.Response> getShop(@PathVariable Long shopKey) {
        return ApiResponse.success(adminShopService.getShop(shopKey));
    }

    @PostMapping
    public ApiResponse<Long> createShop(@RequestBody ShopDTO.Request request) {
        return ApiResponse.success(adminShopService.createShop(request));
    }

    @PutMapping("/{shopKey}")
    public ApiResponse<String> updateShop(@PathVariable Long shopKey, @RequestBody ShopDTO.Request request) {
        adminShopService.updateShop(shopKey, request);
        return ApiResponse.success("상점 정보가 수정되었습니다.");
    }
}

package com.nanum.admin.product.service;

import com.nanum.domain.product.model.*;
import com.nanum.domain.product.repository.InventoryHistoryMasterRepository;
import com.nanum.domain.product.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final ProductStockRepository productStockRepository;
    private final InventoryHistoryMasterRepository inventoryHistoryMasterRepository;

    /**
     * 상품 생성 시 초기 재고(0) 생성
     */
    public void initializeStock(Product product, List<ProductOption> options) {
        if (options == null || options.isEmpty()) {
            // 옵션이 없는 경우 상품 자체 재고 관리 (현재 구조상 옵션이 필수라면 생략 가능하지만 방어 로직)
            ProductStock stock = ProductStock.builder()
                    .product(product)
                    .option(null)
                    .stockQuantity(0)
                    .build();
            productStockRepository.save(stock);
        } else {
            for (ProductOption option : options) {
                ProductStock stock = ProductStock.builder()
                        .product(product)
                        .option(option)
                        .stockQuantity(0)
                        .build();
                productStockRepository.save(stock);
            }
        }
    }

    /**
     * 재고 조정 입출고 처리
     * 
     * @param type      입출고 유형
     * @param productId 상품 ID
     * @param optionId  옵션 ID (Nullable)
     * @param quantity  수량 (양수)
     * @param memo      사유
     */
    public void adjustStock(InventoryType type, Product product, ProductOption option, int quantity, String memo) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Inventory quantity must be positive");
        }

        // 1. Get Current Stock
        ProductStock stock = productStockRepository.findByProductAndOption(product, option)
                .orElseThrow(() -> new IllegalArgumentException("Stock info not found"));

        int prevQty = stock.getStockQuantity();
        int currQty = prevQty;

        // 2. Update Stock
        switch (type) {
            case IN:
            case RETURN:
                stock.increaseStock(quantity);
                currQty = stock.getStockQuantity();
                break;
            case OUT:
            case ADJUST: // For simplicity, ADJUST reduces stock here? Or depends on sign.
                         // Usually ADJUST can be +/-. Assuming OUT logic/Decrease for now or requiring
                         // refinement.
                         // Let's assume ADJUST handles positive quantity as reduction or add specific
                         // method?
                         // User req said "Master/Detail" structure.
                         // Let's strictly follow IN/OUT. For ADJUST, maybe pass signed int?
                         // But quantity is "amount".
                         // Let's implement Decrease for OUT/ADJUST(loss). For Gain, use IN.
                stock.decreaseStock(quantity);
                currQty = stock.getStockQuantity();
                break;
        }

        // 3. Log History
        InventoryHistoryMaster master = InventoryHistoryMaster.builder()
                .memo(memo)
                // .type(type) // Removed from Master
                .build();

        InventoryHistoryDetail detail = InventoryHistoryDetail.builder()
                .master(master)
                .product(product)
                .option(option)
                .type(type)
                .quantity(quantity)
                .prevQuantity(prevQty)
                .currQuantity(currQty)
                .memo(memo)
                .build();

        master.addDetail(detail);
        inventoryHistoryMasterRepository.save(master);
    }
}

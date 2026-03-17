package com.nanum.admin.product.service;

import com.nanum.domain.product.model.*;
import com.nanum.domain.product.repository.InventoryHistoryMasterRepository;
import com.nanum.domain.product.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final ProductStockRepository productStockRepository;
    private final InventoryHistoryMasterRepository inventoryHistoryMasterRepository;

    /**
     * 상품 생성/수정 시 초기 재고 생성 및 전환 대응
     * - 기존 재고(stock_quantity)는 유지함 (Update 시 덮어쓰지 않음)
     * - 전환 시나리오(단품<->옵션) 발생 시 불필요한 기존 데이터 정리 후 신규 생성(0)
     */
    public void initializeStock(Product product, List<ProductOption> options) {
        // 1. 현재 이 상품에 저장된 모든 재고 레코드를 가져옴
        List<ProductStock> existingStocks = productStockRepository.findByProduct(product);

        if (options == null || options.isEmpty()) {
            // [옵션 미사용 상품]
            // 기존에 옵션별 재고가 있었다면 모두 삭제
            List<ProductStock> toDelete = existingStocks.stream()
                    .filter(s -> s.getOption() != null)
                    .collect(java.util.stream.Collectors.toList());
            if (!toDelete.isEmpty()) {
                productStockRepository.deleteAll(toDelete);
            }

            // 기존에 단품 재고가 없으면 새로 생성(0)
            boolean hasBaseStock = existingStocks.stream().anyMatch(s -> s.getOption() == null);
            if (!hasBaseStock) {
                ProductStock stock = ProductStock.builder()
                        .product(product)
                        .option(null)
                        .stockQuantity(0)
                        .build();
                productStockRepository.save(stock);
            } else {
                // [방어 코드] 만약 중복 데이터가 있다면 로그를 남기거나, 첫 번째 것 외에는 활용되지 않음을 명시 (유연한 대응)
                log.debug("상품 {}의 기존 재고 데이터가 존재하여 생성을 건너뜁니다.", product.getId());
            }
        } else {
            // [옵션 사용 상품]
            // 기존에 단품 재고(option_id is null)가 있었다면 삭제
            List<ProductStock> baseStocks = existingStocks.stream()
                    .filter(s -> s.getOption() == null)
                    .collect(java.util.stream.Collectors.toList());
            if (!baseStocks.isEmpty()) {
                productStockRepository.deleteAll(baseStocks);
            }

            // 각 옵션별로 재고 레코드가 없으면 새로 생성(0)
            for (ProductOption option : options) {
                boolean exists = existingStocks.stream()
                        .anyMatch(s -> s.getOption() != null && s.getOption().getId().equals(option.getId()));
                
                if (!exists) {
                    ProductStock stock = ProductStock.builder()
                            .product(product)
                            .option(option)
                            .stockQuantity(0)
                            .build();
                    productStockRepository.save(stock);
                }
            }

            // (추가) 현재 옵션 리스트에 없는 기존 재고 레코드가 있다면 삭제 (옵션 삭제 대응)
            List<Long> currentOptionIds = options.stream()
                    .map(ProductOption::getId)
                    .filter(id -> id != null)
                    .collect(java.util.stream.Collectors.toList());
            
            List<ProductStock> orphanedStocks = existingStocks.stream()
                    .filter(s -> s.getOption() != null && !currentOptionIds.contains(s.getOption().getId()))
                    .collect(java.util.stream.Collectors.toList());
            
            if (!orphanedStocks.isEmpty()) {
                productStockRepository.deleteAll(orphanedStocks);
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

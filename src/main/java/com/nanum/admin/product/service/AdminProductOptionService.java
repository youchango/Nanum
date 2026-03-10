package com.nanum.admin.product.service;

import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductOption;
import com.nanum.domain.product.model.ProductStock;
import com.nanum.domain.product.repository.ProductOptionRepository;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.product.repository.ProductSiteRepository;
import com.nanum.domain.product.repository.ProductStockRepository;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductSiteRepository productSiteRepository;
    private final ProductStockRepository productStockRepository;

    @Transactional
    public void createOption(Long productId, ProductDTO.Option optionRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // 1. Create and save new ProductOption
        ProductOption newOption = ProductOption.builder()
                .product(product)
                .title1(optionRequest.getTitle1())
                .name1(optionRequest.getName1())
                .title2(optionRequest.getTitle2())
                .name2(optionRequest.getName2())
                .title3(optionRequest.getTitle3())
                .name3(optionRequest.getName3())
                .extraPrice(optionRequest.getExtraPrice())
                .stockQuantity(0) // Default to 0 based on user requirement
                .useYn("Y")
                .build();
        productOptionRepository.save(java.util.Objects.requireNonNull(newOption));
        productOptionRepository.flush();

        // 2. Initialize Stock mapping for the new option (0 value)
        ProductStock productStock = ProductStock.builder()
                .product(product)
                .option(newOption)
                .stockQuantity(0)
                .build();
        productStockRepository.save(java.util.Objects.requireNonNull(productStock));
    }

    @Transactional
    public void updateOption(Long productId, Long optionId, ProductDTO.Option optionRequest) {
        ProductOption productOption = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // Validation - ensure the option actually belongs to the provided productId
        if (!productOption.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Mismatch between Product ID and Option ID");
        }

        // Store old extra price to detect changes
        int oldExtraPrice = productOption.getExtraPrice();

        // Update Option entity attributes
        productOption = ProductOption.builder()
                .id(productOption.getId())
                .product(productOption.getProduct())
                .title1(optionRequest.getTitle1())
                .name1(optionRequest.getName1())
                .title2(optionRequest.getTitle2())
                .name2(optionRequest.getName2())
                .title3(optionRequest.getTitle3())
                .name3(optionRequest.getName3())
                .extraPrice(optionRequest.getExtraPrice())
                // Do not override stockQuantity (keep existing to prevent concurrency data
                // loss)
                .stockQuantity(productOption.getStockQuantity())
                .useYn(productOption.getUseYn())
                .build();

        productOptionRepository.save(java.util.Objects.requireNonNull(productOption));
    }

    @Transactional
    public void deleteOption(Long productId, Long optionId) {
        ProductOption productOption = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!productOption.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Mismatch between Product ID and Option ID");
        }

        // Product Option entity hard delete.
        // Underlying ProductOptionSite & ProductStock entries will drop by Cascade
        // constraints.
        productOptionRepository.delete(productOption);
    }
}

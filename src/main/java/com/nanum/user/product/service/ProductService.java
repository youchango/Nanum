package com.nanum.user.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.user.product.dto.ProductDTO;
import com.nanum.user.product.model.Product;
import com.nanum.user.product.model.ProductCategory;
import com.nanum.user.product.model.ProductImage;
import com.nanum.user.product.model.ProductImageType;
import com.nanum.user.product.model.ProductOption;
import com.nanum.user.product.model.ProductStatus;
import com.nanum.user.product.repository.ProductCategoryRepository;
import com.nanum.user.product.repository.ProductRepository;
import com.nanum.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public Long createProduct(ProductDTO.Request request) {
        ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .name(request.getName())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.SALE)
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .deleteYn("N")
                .build();

        // Add Options
        if (request.getOptions() != null) {
            for (ProductDTO.Option optDto : request.getOptions()) {
                ProductOption option = ProductOption.builder()
                        .product(product)
                        .name(optDto.getName())
                        .extraPrice(optDto.getExtraPrice())
                        .stockQuantity(optDto.getStockQuantity())
                        .useYn("Y")
                        .build();
                product.getOptions().add(option);
            }
        }

        // Add Images
        if (request.getImages() != null) {
            for (ProductDTO.Image imgDto : request.getImages()) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imgDto.getImageUrl())
                        .type(ProductImageType.valueOf(imgDto.getType()))
                        .displayOrder(imgDto.getDisplayOrder())
                        .build();
                product.getImages().add(image);
            }
        }

        productRepository.save(product);
        return product.getId();
    }

    @Transactional
    public void updateProduct(Long productId, ProductDTO.Request request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // Note: Full implementation would update options/images too.
        // For brevity, we update main fields here.
        ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        product.updateInfo(
                category,
                request.getName(),
                request.getPrice(),
                request.getSalePrice(),
                request.getStatus(),
                request.getDescription(),
                request.getThumbnailUrl());
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        product.setDeleteYn("Y");
    }

    public List<ProductDTO.Response> getProductList(Long categoryId) {
        // Simple implementation - should be improved with QueryDSL for paging/search
        return productRepository.findAll().stream()
                .filter(p -> "N".equals(p.getDeleteYn()))
                .filter(p -> categoryId == null || p.getCategory().getId().equals(categoryId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductDTO.Response getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        if ("Y".equals(product.getDeleteYn())) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
        }
        return toResponse(product);
    }

    private ProductDTO.Response toResponse(Product product) {
        return ProductDTO.Response.builder()
                .productId(product.getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .status(product.getStatus())
                .thumbnailUrl(product.getThumbnailUrl())
                .build();
    }
}

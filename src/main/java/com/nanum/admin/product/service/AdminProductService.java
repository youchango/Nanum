package com.nanum.admin.product.service;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.*;
import com.nanum.domain.product.repository.ProductCategoryRepository;
import com.nanum.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

        private final ProductRepository productRepository;
        private final ProductCategoryRepository productCategoryRepository;
        private final InventoryService inventoryService;

        public Page<AdminProductListDTO> getProducts(AdminProductSearchDTO searchDTO, Pageable pageable) {
                return productRepository.findAdminProducts(searchDTO, pageable);
        }

        public ProductDTO.Response getProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id));

                return ProductDTO.Response.builder()
                                .productId(product.getId())
                                .categoryName(product.getCategory().getCategoryName())
                                .name(product.getName())
                                .price(product.getPrice())
                                .salePrice(product.getSalePrice())
                                .status(product.getStatus())
                                .thumbnailUrl(product.getThumbnailUrl())
                                .build();
        }

        @Transactional
        public Long createProduct(ProductDTO.Request request) {
                ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

                Product product = Product.builder()
                                .category(category)
                                .name(request.getName())
                                .price(request.getPrice())
                                .salePrice(request.getSalePrice())
                                .status(request.getStatus())
                                .description(request.getDescription())
                                .thumbnailUrl(request.getThumbnailUrl())
                                .deleteYn("N")
                                .build();

                // Options
                if (request.getOptions() != null) {
                        List<ProductOption> options = request.getOptions().stream()
                                        .map(opt -> ProductOption.builder()
                                                        .product(product)
                                                        .name(opt.getName())
                                                        .extraPrice(opt.getExtraPrice())
                                                        .stockQuantity(opt.getStockQuantity())
                                                        .useYn("Y")
                                                        .build())
                                        .collect(Collectors.toList());
                        product.getOptions().addAll(options);
                }

                // Images
                if (request.getImages() != null) {
                        List<ProductImage> images = request.getImages().stream()
                                        .map(img -> ProductImage.builder()
                                                        .product(product)
                                                        .imageUrl(img.getImageUrl())
                                                        .type(ProductImageType.valueOf(img.getType()))
                                                        .displayOrder(img.getDisplayOrder())
                                                        .build())
                                        .collect(Collectors.toList());
                        product.getImages().addAll(images);
                }

                productRepository.save(product);

                // Initialize Stock (Warehouse)
                inventoryService.initializeStock(product, product.getOptions());

                return product.getId();
        }

        @Transactional
        public void updateProduct(Long id, ProductDTO.Request request) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

                // Basic Info Update
                product.updateInfo(
                                category,
                                request.getName(),
                                request.getPrice(),
                                request.getSalePrice(),
                                request.getStatus(),
                                request.getDescription(),
                                request.getThumbnailUrl());

                // Options Update (Clear and Add)
                // Note: This deletes old options and stock (via cascade).
                product.getOptions().clear();
                // Since we need to initialize stock for new options, we capture them.
                if (request.getOptions() != null) {
                        List<ProductOption> options = request.getOptions().stream()
                                        .map(opt -> ProductOption.builder()
                                                        .product(product)
                                                        .name(opt.getName())
                                                        .extraPrice(opt.getExtraPrice())
                                                        .stockQuantity(opt.getStockQuantity())
                                                        .useYn("Y")
                                                        .build())
                                        .collect(Collectors.toList());
                        product.getOptions().addAll(options);
                }

                // Images Update (Clear and Add)
                product.getImages().clear();
                if (request.getImages() != null) {
                        List<ProductImage> images = request.getImages().stream()
                                        .map(img -> ProductImage.builder()
                                                        .product(product)
                                                        .imageUrl(img.getImageUrl())
                                                        .type(ProductImageType.valueOf(img.getType()))
                                                        .displayOrder(img.getDisplayOrder())
                                                        .build())
                                        .collect(Collectors.toList());
                        product.getImages().addAll(images);
                }

                // Flush to ensure options have IDs before creating stock?
                // Or simply save product.
                productRepository.save(product);

                // Initialize Stock for new options
                inventoryService.initializeStock(product, product.getOptions());
        }

        @Transactional
        public void updateProductStatus(Long id, ProductStatus status) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                product.update(product.getName(), product.getPrice(), product.getSalePrice(), product.getDescription(),
                                product.getThumbnailUrl(), status);
        }

        @Transactional
        public void deleteProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                product.setDeleteYn("Y");
        }
}

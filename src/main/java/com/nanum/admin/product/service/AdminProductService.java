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
        private final com.nanum.domain.file.service.FileService fileService;

        public Page<AdminProductListDTO> getProducts(AdminProductSearchDTO searchDTO, Pageable pageable) {
                return productRepository.findAdminProducts(searchDTO, pageable);
        }

        public ProductDTO.Response getProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id));

                List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(id))
                                .stream()
                                .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                                .collect(Collectors.toList());

                return ProductDTO.Response.builder()
                                .productId(product.getId())
                                .categoryName(product.getCategory().getCategoryName())
                                .name(product.getName())
                                .price(product.getPrice())
                                .salePrice(product.getSalePrice())
                                .status(product.getStatus())
                                .files(files)
                                .build();
        }

        @Transactional
        public Long createProduct(ProductDTO.Request request) {
                ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

                // 1. Save Product
                Product product = Product.builder()
                                .category(category)
                                .name(request.getName())
                                .price(request.getPrice())
                                .salePrice(request.getSalePrice())
                                .status(request.getStatus())
                                .description(request.getDescription())
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

                productRepository.save(product);

                // 2. Link Files (FileStore)
                processFiles(request.getImages(), product.getId());

                // Initialize Stock
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
                                request.getDescription());

                // Options Update
                product.getOptions().clear();
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

                // Files Update
                processFiles(request.getImages(), id);

                productRepository.save(product);
                inventoryService.initializeStock(product, product.getOptions());
        }

        private void processFiles(List<ProductDTO.Image> images, Long productId) {
                if (images == null || images.isEmpty())
                        return;

                List<String> fileIds = images.stream()
                                .map(ProductDTO.Image::getFileId)
                                .filter(id -> id != null && !id.isEmpty())
                                .collect(Collectors.toList());

                com.nanum.domain.file.model.ReferenceType type = com.nanum.domain.file.model.ReferenceType.PRODUCT;
                fileService.updateFileReference(fileIds, type, String.valueOf(productId));

                for (ProductDTO.Image img : images) {
                        if (img.getFileId() != null && "MAIN".equals(img.getType())) {
                                fileService.setMainImage(img.getFileId(), type, String.valueOf(productId));
                        }
                }
        }

        @Transactional
        public void updateProductStatus(Long id, ProductStatus status) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                product.update(product.getName(), product.getPrice(), product.getSalePrice(), product.getDescription(),
                                status);
        }

        @Transactional
        public void deleteProduct(Long id, String memberCode) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                product.delete(memberCode);
                fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(id),
                                memberCode);
        }
}

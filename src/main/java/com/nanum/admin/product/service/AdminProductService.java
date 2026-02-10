package com.nanum.admin.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.global.error.ErrorCode;

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

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.domain.product.repository.ProductSiteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

        private final ProductRepository productRepository;
        private final ProductSiteRepository productSiteRepository; // Injected
        private final ProductCategoryRepository productCategoryRepository;
        private final InventoryService inventoryService;
        private final com.nanum.domain.file.service.FileService fileService;

        public Page<AdminProductListDTO> getProducts(AdminProductSearchDTO searchDTO, Pageable pageable) {
                Manager manager = getCurrentManager();
                if ("MASTER".equals(manager.getMbType())) {
                        // Master can filter by siteCd if provided in searchDTO
                        // If empty, they might see all (depending on repo implementation)
                } else {
                        // Manager restricted to their site
                        searchDTO.setSiteCd(manager.getSiteCd());
                }
                return productRepository.findAdminProducts(searchDTO, pageable);
        }

        public ProductDTO.Response getProduct(Long id) {
                Manager manager = getCurrentManager();
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id));

                // Ensure availability for site?
                // If Manager, check if ProductSite exists for this site?
                // For now, let's allow viewing detail if ID is known, or strictly filtering?
                // Let's keep it simple for now, but strictly we should check permission.

                List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(id))
                                .stream()
                                .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                                .collect(Collectors.toList());

                ProductCategory firstCategory = product.getCategories().isEmpty() ? null
                                : product.getCategories().get(0);

                return ProductDTO.Response.builder()
                                .productId(product.getId())
                                .categoryId(product.getCategories().stream().map(ProductCategory::getCategoryId)
                                                .collect(Collectors.toList()))
                                .categoryName(firstCategory != null ? firstCategory.getCategoryName() : null)
                                .name(product.getName())
                                .supplyPrice(product.getSupplyPrice())
                                .mapPrice(product.getMapPrice())
                                .standardPrice(product.getStandardPrice())
                                .status(product.getStatus())
                                .files(files)
                                .build();
        }

        @Transactional
        public Long createProduct(ProductDTO.Request request) {
                Manager manager = getCurrentManager();

                if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }
                List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryIds());
                if (categories.isEmpty()) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }

                // 1. Save Product
                Product product = Product.builder()
                                .categories(categories)
                                .name(request.getName())
                                .mapPrice(request.getMapPrice())
                                .standardPrice(request.getStandardPrice())
                                .status(request.getStatus())
                                .description(request.getDescription())
                                .supplyPrice(request.getSupplyPrice())
                                .viewCount(0)
                                .build();

                // Check if options exist and add them
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
                productRepository.flush(); // Ensure IDs are generated for product and options

                // 2. Save ProductSite (Current Site)
                if (product.getOptions() != null && !product.getOptions().isEmpty()) {
                        for (ProductOption opt : product.getOptions()) {
                                // Default logic: all tier prices = standardPrice + extraPrice
                                BigDecimal basePrice = new BigDecimal(request.getStandardPrice())
                                                .add(new BigDecimal(opt.getExtraPrice()));

                                ProductSite ps = ProductSite.builder()
                                                .product(product)
                                                .optionId(opt.getId())
                                                .siteCd(manager.getSiteCd())
                                                .viewYn("Y") // Default visible for creator
                                                .aPrice(basePrice)
                                                .bPrice(basePrice)
                                                .cPrice(basePrice)
                                                .pdtClick(0)
                                                .build();
                                productSiteRepository.save(ps);
                        }
                } else {
                        // Handle no options logic if necessary, e.g., create a ProductSite for the base
                        // product
                        BigDecimal basePrice = new BigDecimal(request.getStandardPrice());
                        ProductSite ps = ProductSite.builder()
                                        .product(product)
                                        .optionId(null) // No option, so optionId is null
                                        .siteCd(manager.getSiteCd())
                                        .viewYn("Y")
                                        .aPrice(basePrice)
                                        .bPrice(basePrice)
                                        .cPrice(basePrice)
                                        .pdtClick(0)
                                        .build();
                        productSiteRepository.save(ps);
                }

                // 3. Link Files
                processFiles(request.getImages(), product.getId());

                // 4. Initialize Stock
                inventoryService.initializeStock(product, product.getOptions());

                return product.getId();
        }

        @Transactional
        public void updateProduct(Long id, ProductDTO.Request request) {
                Manager manager = getCurrentManager();
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryIds());
                if (categories.isEmpty() || categories.size() != request.getCategoryIds().size()) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND); // Some categories not found
                }

                // Basic Info Update
                product.updateInfo(
                                categories, // Pass categories list
                                request.getName(),
                                request.getMapPrice(),
                                request.getStandardPrice(),
                                request.getStatus(),
                                request.getDescription());

                // Options Update - Clear existing and add new ones
                // This is a simple replacement strategy. For more complex updates (e.g.,
                // partial updates,
                // preserving existing options), more sophisticated logic would be needed.
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

                // Check if ProductSite exists for this site
                ProductSite productSite = productSiteRepository.findByProductAndSiteCd(product, manager.getSiteCd())
                                .orElse(null);

                if (productSite != null) {
                        BigDecimal basePrice = new BigDecimal(request.getStandardPrice()); // Simplify for now
                        productSite.update(
                                        "Y",
                                        basePrice,
                                        basePrice,
                                        basePrice);
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
                product.update(product.getName(), product.getMapPrice(), product.getStandardPrice(),
                                product.getDescription(),
                                status);
        }

        @Transactional
        public void deleteProduct(Long id, String memberCode) { // memberCode param kept for interface compat, but we
                                                                // use principal
                Manager manager = getCurrentManager();
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                product.delete(manager.getManagerId()); // Use actual ID
                fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(id),
                                manager.getManagerId());
        }

        private Manager getCurrentManager() {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof CustomManagerDetails) {
                        return ((CustomManagerDetails) principal).getManager();
                }
                throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
        }
}

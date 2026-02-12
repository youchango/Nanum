package com.nanum.user.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductCategory;
import com.nanum.domain.product.model.ProductOption;
import com.nanum.domain.product.model.ProductStatus;
import com.nanum.domain.product.repository.ProductCategoryRepository;
import com.nanum.domain.product.repository.ProductRepository;
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
    private final com.nanum.domain.file.service.FileService fileService;

    @Transactional
    public Long createProduct(ProductDTO.Request request) {
        if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
        }
        List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryIds());

        Product product = Product.builder()
                .categories(categories)
                .name(request.getName())
                .mapPrice(request.getMapPrice())
                .standardPrice(request.getStandardPrice())
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.SALE)
                .description(request.getDescription())
                .supplyPrice(0) // Default for user side creation? Or maybe not needed if nullable? Entity says
                                // nullable=false, default 0.
                .viewCount(0)
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

        productRepository.save(product);

        // Link Files
        processFiles(request.getImages(), product.getId());

        return product.getId();
    }

    @Transactional
    public void updateProduct(Long productId, ProductDTO.Request request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryIds());

        product.updateInfo(
                categories,
                request.getName(),
                request.getMapPrice(),
                request.getStandardPrice(),
                request.getStatus(),
                request.getDescription());

        // Update Files
        processFiles(request.getImages(), productId);
    }

    @Transactional
    public void deleteProduct(Long productId, String memberCode) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        product.delete(memberCode);
        fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(productId),
                memberCode);
    }

    public List<ProductDTO.Response> getProductList(Long categoryId) {
        // Simple implementation - should be improved with QueryDSL for paging/search
        return productRepository.findAll().stream()
                .filter(p -> "N".equals(p.getDeleteYn()))
                .filter(p -> categoryId == null
                        || p.getCategories().stream().anyMatch(c -> c.getCategoryId().equals(categoryId)))
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
        List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(product.getId()))
                .stream()
                .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                .collect(Collectors.toList());

        ProductCategory firstCategory = product.getCategories().isEmpty() ? null : product.getCategories().get(0);

        return ProductDTO.Response.builder()
                .productId(product.getId())
                .categoryId(product.getCategories().stream().map(ProductCategory::getCategoryId)
                        .collect(Collectors.toList()))
                .categoryName(firstCategory != null ? firstCategory.getCategoryName() : null)
                .name(product.getName())
                .mapPrice(product.getMapPrice())
                .standardPrice(product.getStandardPrice())
                .status(product.getStatus())
                .files(files)
                .build();
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
}

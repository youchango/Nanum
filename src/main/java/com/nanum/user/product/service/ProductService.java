package com.nanum.user.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.Product;
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
        private final com.nanum.domain.file.service.FileService fileService;

        public List<ProductDTO.Response> getProductList(Long categoryId) {
                // Simple implementation - should be improved with QueryDSL for paging/search
                return productRepository.findAll().stream()
                                .filter(p -> "N".equals(p.getDeleteYn()))
                                .filter(p -> categoryId == null
                                                || p.getCategories().stream()
                                                                .anyMatch(c -> c.getCategoryId().equals(categoryId)))
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
                List<ProductDTO.Image> images = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT,
                                                String.valueOf(product.getId()))
                                .stream()
                                .map(f -> ProductDTO.Image.builder()
                                                .fileId(f.getFileId())
                                                .imageUrl(f.getPath())
                                                .type(f.getReferenceType().name())
                                                .displayOrder(f.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());

                com.nanum.domain.product.model.ProductCategory firstCategory = product.getCategories().isEmpty() ? null
                                : product.getCategories().get(0);

                return ProductDTO.Response.builder()
                                .productId(product.getId())
                                .categoryId(product.getCategories().stream().map(c -> c.getCategoryId())
                                                .collect(Collectors.toList()))
                                .categoryName(firstCategory != null ? firstCategory.getCategoryName() : null)
                                .name(product.getName())
                                .mapPrice(product.getMapPrice())
                                .standardPrice(product.getStandardPrice())
                                .status(product.getStatus())
                                .images(images)
                                .build();
        }
}

package com.nanum.admin.product.service;

import com.nanum.user.product.model.ProductCategory;
import com.nanum.product.model.ProductCategoryDTO;
import com.nanum.user.product.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    // 카테고리 생성
    @Transactional
    public ProductCategory createCategory(ProductCategoryDTO dto) {
        ProductCategory parent = null;
        if (dto.getParentId() != null) {
            parent = productCategoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
        }

        ProductCategory category = ProductCategory.builder()
                .categoryName(dto.getCategoryName())
                .depth(dto.getDepth())
                .displayOrder(dto.getDisplayOrder())
                .useYn(dto.getUseYn() == null ? "Y" : dto.getUseYn())
                .parent(parent)
                .build();

        return productCategoryRepository.save(category);
    }

    // 카테고리 수정
    @Transactional
    public ProductCategory updateCategory(Long id, ProductCategoryDTO dto) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setCategoryName(dto.getCategoryName());
        category.setDisplayOrder(dto.getDisplayOrder());
        category.setUseYn(dto.getUseYn());

        // 부모 변경 로직이 필요하다면 추가 (일반적으로 카테고리 트리 이동은 복잡하므로 여기선 제외 가능하거나 별도 처리)
        if (dto.getParentId() != null) {
            ProductCategory parent = productCategoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
            category.setDepth(parent.getDepth() + 1);
        }

        return category; // Dirty checking update
    }

    // 카테고리 삭제 (하위 카테고리가 있으면 삭제 불가 정책 등 고려)
    @Transactional
    public void deleteCategory(Long id) {
        productCategoryRepository.deleteById(id);
    }

    // 사용 여부 토글 (편의 기능)
    @Transactional
    public void toggleUseYn(Long id, String useYn) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setUseYn(useYn);
    }

    // 전체 카테고리 트리 조회 (Admin용)
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> getAllCategories() {
        // 모든 카테고리 조회 후 트리 구조로 변환하는 로직 구현 필요
        // 간단하게는 전체 목록 반환하거나, Root만 조회해서 Children fetch
        List<ProductCategory> all = productCategoryRepository.findAll();
        // DTO 변환 로직 (단순 매핑 예시)
        return all.stream().map(c -> new ProductCategoryDTO(
                c.getCategoryId(),
                c.getParent() != null ? c.getParent().getCategoryId() : null,
                c.getCategoryName(),
                c.getDepth(),
                c.getDisplayOrder(),
                c.getUseYn(),
                null // Children은 별도 로직으로 조립
        )).collect(Collectors.toList());
    }
}

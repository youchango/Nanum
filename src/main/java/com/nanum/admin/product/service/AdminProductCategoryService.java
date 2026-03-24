package com.nanum.admin.product.service;

import com.nanum.domain.product.dto.ProductCategoryDTO;
import com.nanum.domain.product.model.ProductCategory;
import com.nanum.domain.product.repository.ProductCategoryRepository;
import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import com.nanum.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final com.nanum.domain.product.repository.ProductRepository productRepository;
    private final FileService fileService;

    /**
     * 카테고리 생성
     *
     * @param dto 카테고리 정보
     * @return 생성된 카테고리
     */
    @Transactional
    public ProductCategory createCategory(ProductCategoryDTO dto) {
        ProductCategory parent = null;
        if (dto.getParentId() != null) {
            parent = productCategoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리를 찾을 수 없습니다."));
        }

        ProductCategory category = ProductCategory.builder()
                .categoryName(dto.getCategoryName())
                .depth(dto.getDepth())
                .displayOrder(dto.getDisplayOrder())
                .useYn(dto.getUseYn() == null ? "Y" : dto.getUseYn())
                .parent(parent)
                .build();

        ProductCategory saved = productCategoryRepository.save(category);

        if (dto.getImageFileId() != null && !dto.getImageFileId().isEmpty()) {
            fileService.syncFiles(Collections.singletonList(dto.getImageFileId()), ReferenceType.CATEGORY, String.valueOf(saved.getCategoryId()));
        }

        return saved;
    }

    /**
     * 카테고리 수정
     *
     * @param id  카테고리 ID
     * @param dto 수정할 정보
     * @return 수정된 카테고리
     */
    @Transactional
    public ProductCategory updateCategory(Long id, ProductCategoryDTO dto) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        category.setCategoryName(dto.getCategoryName());
        category.setDisplayOrder(dto.getDisplayOrder());
        category.setUseYn(dto.getUseYn());

        // 파일 연동 로직
        if (dto.getImageFileId() != null && !dto.getImageFileId().isEmpty()) {
            // 새 파일이 지정된 경우
            fileService.syncFiles(Collections.singletonList(dto.getImageFileId()), ReferenceType.CATEGORY, String.valueOf(category.getCategoryId()));
        } else if (dto.getImageUrl() == null || dto.getImageUrl().isEmpty()) {
            // 이미지가 명시적으로 삭제된 경우
            fileService.syncFiles(Collections.emptyList(), ReferenceType.CATEGORY, String.valueOf(category.getCategoryId()));
        } else {
            // 기존 이미지 유지
        }

        // 상위 카테고리 변경 시 Depth 및 Parent 업데이트
        if (dto.getParentId() != null) {
            // 본인을 부모로 설정하는지 체크 (순환 참조 방지)
            if (dto.getParentId().equals(id)) {
                throw new IllegalArgumentException("자기 자신을 상위 카테고리로 지정할 수 없습니다.");
            }
            ProductCategory parent = productCategoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리를 찾을 수 없습니다."));
            category.setParent(parent);
            category.setDepth(parent.getDepth() + 1);
        } else {
            // Root로 이동
            category.setParent(null);
            category.setDepth(1);
        }

        return category;
    }

    /**
     * 카테고리 삭제
     * 하위 카테고리가 존재할 경우 삭제가 불가능합니다.
     *
     * @param id 카테고리 ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        if (productCategoryRepository.existsByParent(category)) {
            throw new IllegalStateException("하위 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        if (productRepository.existsByCategoriesContainsAndDeleteYn(category, "N")) {
            throw new IllegalStateException("해당 카테고리에 속한 상품이 존재하여 삭제할 수 없습니다.");
        }

        fileService.syncFiles(Collections.emptyList(), ReferenceType.CATEGORY, String.valueOf(category.getCategoryId()));
        productCategoryRepository.delete(category);
    }

    /**
     * 카테고리 사용 여부 변경
     *
     * @param id    카테고리 ID
     * @param useYn 사용 여부 (Y/N)
     */
    @Transactional
    public void toggleUseYn(Long id, String useYn) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        category.setUseYn(useYn);
    }

    /**
     * 전체 카테고리 목록 조회 (계층형 트리 구조)
     *
     * @return 계층형 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> getAllCategories() {
        // 1. 전체 카테고리 조회 (Depth, DisplayOrder 순 정렬)
        List<ProductCategory> allCategories = productCategoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "depth", "displayOrder"));

        // 1.5. 이미지 URL 정보 일괄 로드 (N+1 방지)
        List<String> categoryIds = allCategories.stream()
                .map(c -> String.valueOf(c.getCategoryId()))
                .collect(Collectors.toList());
        List<FileStore> files = fileService.getFiles(ReferenceType.CATEGORY, categoryIds);
        Map<String, String> imageUrlMap = new HashMap<>();
        for (FileStore file : files) {
            imageUrlMap.put(file.getReferenceId(), fileService.getFullUrl(file.getPath()));
        }

        // 2. DTO 변환 및 Map핑 (ID -> DTO)
        Map<Long, ProductCategoryDTO> dtoMap = new HashMap<>();
        List<ProductCategoryDTO> rootCategories = new ArrayList<>();

        for (ProductCategory category : allCategories) {
            ProductCategoryDTO dto = new ProductCategoryDTO(
                    category.getCategoryId(),
                    category.getParent() != null ? category.getParent().getCategoryId() : null,
                    category.getCategoryName(),
                    category.getDepth(),
                    category.getDisplayOrder(),
                    category.getUseYn(),
                    imageUrlMap.get(String.valueOf(category.getCategoryId())), // imageUrl
                    null, // imageFileId
                    new ArrayList<>() // Children 초기화
            );
            dtoMap.put(dto.getCategoryId(), dto);
        }

        // 3. 계층 구조 조립
        for (ProductCategory category : allCategories) {
            ProductCategoryDTO currentDto = dtoMap.get(category.getCategoryId());
            if (category.getParent() == null) {
                // Root Node
                rootCategories.add(currentDto);
            } else {
                Long parentId = category.getParent().getCategoryId();
                ProductCategoryDTO parentDto = dtoMap.get(parentId);

                if (parentDto != null) {
                    // 무한 순환 참조(StackOverflow) 500 에러 방어 로직
                    boolean isCyclic = false;
                    ProductCategoryDTO temp = parentDto;
                    while (temp != null) {
                        if (temp.getCategoryId().equals(currentDto.getCategoryId())) {
                            isCyclic = true;
                            break;
                        }
                        temp = temp.getParentId() != null ? dtoMap.get(temp.getParentId()) : null;
                    }

                    if (!isCyclic) {
                        parentDto.getChildren().add(currentDto);
                    } else {
                        // 순환이 감지되면 끊어버리고 Root 취급
                        currentDto.setParentId(null);
                        rootCategories.add(currentDto);
                    }
                } else {
                    // 고아 노드 방어 (부모가 DB 리스트에 없는 경우) 강제 Root 취급
                    currentDto.setParentId(null);
                    rootCategories.add(currentDto);
                }
            }
        }

        return rootCategories;
    }
}

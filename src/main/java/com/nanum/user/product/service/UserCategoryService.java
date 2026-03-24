package com.nanum.user.product.service;

import com.nanum.domain.product.dto.UserCategoryDTO;
import com.nanum.domain.product.model.ProductCategory;
import com.nanum.domain.product.repository.ProductCategoryRepository;
import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import com.nanum.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final FileService fileService;

    public List<UserCategoryDTO> getCategoryTree() {
        // 1. Fetch all categories ordered by display order
        List<ProductCategory> allCategories = productCategoryRepository.findAllByOrderByDisplayOrderAsc();

        // 2. Load category image URLs in bulk to prevent N+1
        List<String> categoryIds = allCategories.stream()
                .map(c -> String.valueOf(c.getCategoryId()))
                .collect(Collectors.toList());

        List<FileStore> files = fileService.getFiles(ReferenceType.CATEGORY, categoryIds);
        Map<String, String> imageUrlMap = new HashMap<>();
        for (FileStore file : files) {
            // Apply full url mapping for frontend src usage
            imageUrlMap.put(file.getReferenceId(), fileService.getFullUrl(file.getPath()));
        }

        // 3. Filter root categories (depth 1 / parent == null) and map recursively
        return allCategories.stream()
                .filter(c -> "Y".equals(c.getUseYn()) && c.getParent() == null)
                .map(c -> UserCategoryDTO.from(c, imageUrlMap))
                .collect(Collectors.toList());
    }
}

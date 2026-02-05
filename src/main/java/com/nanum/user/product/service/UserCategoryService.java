package com.nanum.user.product.service;

import com.nanum.domain.product.dto.UserCategoryDTO;
import com.nanum.domain.product.model.ProductCategory;
import com.nanum.domain.product.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public List<UserCategoryDTO> getCategoryTree() {
        // Fetch all categories ordered by display order
        List<ProductCategory> allCategories = productCategoryRepository.findAllByOrderByDisplayOrderAsc();

        // Filter root categories (depth 1) and active ones
        return allCategories.stream()
                .filter(c -> c.getDepth() == 1 && "Y".equals(c.getUseYn()))
                .map(UserCategoryDTO::from) // Recursive mapping in DTO
                .collect(Collectors.toList());
    }
}

package com.nanum.domain.product.dto;

import com.nanum.domain.product.model.ProductCategory; // Add missing import for self-referencing list if needed, but wait, List<ProductCategoryDTO> does not need it.
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDTO {
    private Long categoryId;
    private Long parentId;
    private String categoryName;
    private int depth;
    private int displayOrder;
    private String useYn;
    private List<ProductCategoryDTO> children;
}

package com.nanum.product.model;

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

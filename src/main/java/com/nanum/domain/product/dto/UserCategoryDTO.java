package com.nanum.domain.product.dto;

import com.nanum.domain.product.model.ProductCategory;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryDTO {

    private Long parentId;
    private Long categoryId;
    private String categoryName;
    private int depth;
    private int displayOrder;
    private String imageUrl;
    private List<UserCategoryDTO> children;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static UserCategoryDTO from(ProductCategory category, Map<String, String> imageUrlMap) {
        return UserCategoryDTO.builder()
                .parentId(category.getParent() != null ? category.getParent().getCategoryId() : null)
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .depth(category.getDepth())
                .displayOrder(category.getDisplayOrder())
                .imageUrl(imageUrlMap != null ? imageUrlMap.get(String.valueOf(category.getCategoryId())) : null)
                // Children conversion if loaded, sorted by displayOrder
                .children(category.getChildren() != null ? category.getChildren().stream()
                        .filter(c -> "Y".equals(c.getUseYn())) // Only active children
                        .sorted((c1, c2) -> Integer.compare(c1.getDisplayOrder(), c2.getDisplayOrder()))
                        .map(c -> UserCategoryDTO.from(c, imageUrlMap))
                        .collect(Collectors.toList()) : null)
                .build();
    }
}

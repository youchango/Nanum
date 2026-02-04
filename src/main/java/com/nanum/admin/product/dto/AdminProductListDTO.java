package com.nanum.admin.product.dto;

import com.nanum.user.product.model.ProductStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AdminProductListDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private int price;
    private int salePrice;
    private ProductStatus status;
    private String thumbnailUrl;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String deleteYn;
}

package com.nanum.domain.product.dto;

import com.nanum.domain.product.model.ProductStatus;
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
    private int supplyPrice;
    private int mapPrice;
    private int standardPrice;
    private ProductStatus status;

    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String deleteYn;
}

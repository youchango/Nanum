package com.nanum.domain.product.dto;

import com.nanum.domain.product.model.ProductStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Integer mapPrice;
    private Integer retailPrice;
    private Integer suggestedPrice;
    private Integer safetyStock;
    private Integer stockQuantity;
    private String optionYn;
    private ProductStatus status;
    private String applyYn;

    // To check stock level logic
    @JsonProperty("options")
    private java.util.List<ProductDTO.Option> options;

    // From ProductSite (1:N options mapping)
    @JsonProperty("sitePrices")
    private java.util.List<ProductSitePriceDTO> sitePrices;

    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String deleteYn;
}

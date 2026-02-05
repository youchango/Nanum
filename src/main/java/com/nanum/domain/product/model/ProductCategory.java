package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductCategory parent;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(nullable = false)
    @ColumnDefault("1")
    private int depth;

    @Column(name = "display_order", nullable = false)
    @ColumnDefault("0")
    private int displayOrder;

    @Column(name = "use_yn", nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<ProductCategory> children = new ArrayList<>();

}

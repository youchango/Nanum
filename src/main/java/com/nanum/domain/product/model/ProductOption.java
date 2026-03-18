package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_title1", length = 50)
    private String title1;

    @Column(name = "option_name1", length = 100)
    private String name1;

    @Column(name = "option_title2", length = 50)
    private String title2;

    @Column(name = "option_name2", length = 100)
    private String name2;

    @Column(name = "option_title3", length = 50)
    private String title3;

    @Column(name = "option_name3", length = 100)
    private String name3;

    @Column(name = "extra_price", nullable = false)
    @ColumnDefault("0")
    private int extraPrice;

    @Column(name = "stock_quantity", nullable = false)
    @ColumnDefault("0")
    private int stockQuantity; // 사용자 노출용 재고 (Display Stock)

    @Column(name = "use_yn", nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;

    /**
     * 옵션 정보를 갱신합니다.
     *
     * @param title1        옵션분류1
     * @param name1         옵션값1
     * @param title2        옵션분류2
     * @param name2         옵션값2
     * @param title3        옵션분류3
     * @param name3         옵션값3
     * @param extraPrice    추가 금액
     * @param stockQuantity 재고 수량
     */
    public void update(String title1, String name1, String title2, String name2, String title3, String name3,
            int extraPrice, int stockQuantity) {
        this.title1 = title1;
        this.name1 = name1;
        this.title2 = title2;
        this.name2 = name2;
        this.title3 = title3;
        this.name3 = name3;
        this.extraPrice = extraPrice;
        this.stockQuantity = stockQuantity;
    }

    /**
     * 옵션 기본 정보를 갱신합니다. (재고 제외)
     *
     * @param title1        옵션분류1
     * @param name1         옵션값1
     * @param title2        옵션분류2
     * @param name2         옵션값2
     * @param title3        옵션분류3
     * @param name3         옵션값3
     * @param extraPrice    추가 금액
     */
    public void updateBasicInfo(String title1, String name1, String title2, String name2, String title3, String name3,
            int extraPrice) {
        this.title1 = title1;
        this.name1 = name1;
        this.title2 = title2;
        this.name2 = name2;
        this.title3 = title3;
        this.name3 = name3;
        this.extraPrice = extraPrice;
    }

    /**
     * 재고 수량을 업데이트합니다.
     * @param stockQuantity 새로운 재고 수량
     */
    public void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}

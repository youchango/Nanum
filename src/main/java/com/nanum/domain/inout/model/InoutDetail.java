package com.nanum.domain.inout.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 입출고 상세 엔티티
 * 개별 품목의 입출고 내역을 관리합니다.
 */
@Entity
@Table(name = "inout_detail")
@IdClass(InoutDetailId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE inout_detail SET delete_yn = 'Y', deleted_at = NOW() WHERE io_code = ? AND no = ?")
@Where(clause = "delete_yn = 'N'")
public class InoutDetail extends BaseEntity {

    @Id
    @Column(name = "io_code", length = 45)
    private String ioCode;

    @Id
    @Column(name = "no")
    private Integer no;

    @Column(name = "io_type", nullable = false, length = 10)
    private String ioType;

    @Column(name = "in_io_code", length = 45)
    private String inIoCode;

    @Column(name = "in_no")
    private Integer inNo;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "option_name", length = 200)
    private String optionName;

    @Column(name = "brand_name", length = 100)
    private String brandName;

    @Column(name = "qty")
    @Builder.Default
    private Integer qty = 0;

    @Column(name = "real_qty")
    @Builder.Default
    private Integer realQty = 0;

    @Column(name = "memo", length = 200)
    private String memo;

    /**
     * 입고 잔량 업데이트 (출고 시 사용)
     * @param subtractQty 차감할 수량
     */
    public void decreaseRealQty(Integer subtractQty) {
        if (this.realQty < subtractQty) {
            throw new IllegalArgumentException("출고 수량이 입고 잔량보다 많을 수 없습니다.");
        }
        this.realQty -= subtractQty;
    }
}

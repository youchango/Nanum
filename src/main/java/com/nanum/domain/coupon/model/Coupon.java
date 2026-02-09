package com.nanum.domain.coupon.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(name = "site_cd", length = 20)
    @ColumnDefault("'SITECD000001'")
    private String siteCd;

    @Column(name = "coupon_name", nullable = false, length = 100)
    private String name;

    @Column(name = "discount_type", nullable = false, length = 10)
    @ColumnDefault("'FIXED'")
    private String discountType; // FIXED, RATE

    @Column(name = "discount_value", nullable = false)
    private Integer discountValue;

    @Column(name = "max_discount")
    private Integer maxDiscount;

    @Column(name = "min_order_price", nullable = false)
    @ColumnDefault("0")
    private Integer minOrderPrice;

    @Column(name = "valid_start_date", nullable = false)
    private LocalDateTime validStartDate;

    @Column(name = "valid_end_date", nullable = false)
    private LocalDateTime validEndDate;

    @Column(name = "target_type", nullable = false, length = 20)
    @ColumnDefault("'ALL'")
    private String targetType; // ALL, USER, BIZ

    @Column(name = "issue_limit")
    private Integer issueLimit;

    @Column(name = "issue_count", nullable = false)
    @ColumnDefault("0")
    private Integer issueCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;
}

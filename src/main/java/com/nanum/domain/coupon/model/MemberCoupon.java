package com.nanum.domain.coupon.model;

import com.nanum.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_coupon")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "used_yn", length = 1, nullable = false)
    @ColumnDefault("'N'")
    @Builder.Default
    private String usedYn = "N";

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "order_id")
    private Long orderId;

    @CreatedDate
    @Column(name = "issued_at", updatable = false)
    private LocalDateTime issuedAt;

    /**
     * 쿠폰 사용 처리
     */
    public void markUsed(Long orderId) {
        this.usedYn = "Y";
        this.usedAt = LocalDateTime.now();
        this.orderId = orderId;
    }
}

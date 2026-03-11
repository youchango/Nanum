package com.nanum.domain.wishlist.model;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wishlist", uniqueConstraints = {
        @UniqueConstraint(name = "uq_wishlist_user_prod", columnNames = { "member_code", "product_id" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist extends com.nanum.global.common.dto.BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long wishlistId;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public Wishlist(Member member, Product product) {
        this.member = member;
        this.product = product;
    }
}

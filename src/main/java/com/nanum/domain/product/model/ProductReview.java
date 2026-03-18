package com.nanum.domain.product.model;

import com.nanum.domain.member.model.Member;
import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "product_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product_review SET delete_yn = 'Y', deleted_at = NOW() WHERE review_id = ?")
public class ProductReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code", nullable = false)
    private Member member;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    // 상품 리뷰 정보 수정
    public void updateInfo(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    // 좋아요 수 증가
    public void addLike() {
        this.likeCount++;
    }

    // 좋아요 수 감소
    public void removeLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}

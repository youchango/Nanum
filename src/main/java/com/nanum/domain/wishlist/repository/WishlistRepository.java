package com.nanum.domain.wishlist.repository;

import com.nanum.domain.wishlist.model.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

        @Query("SELECT COUNT(w) > 0 FROM Wishlist w WHERE w.member.memberCode = :memberCode AND w.product.id = :productId AND w.siteCd = :siteCd AND w.deleteYn = 'N'")
        boolean existsByMemberMemberCodeAndProduct_Id(@Param("memberCode") String memberCode,
                        @Param("productId") Long productId,
                        @Param("siteCd") String siteCd);

        @Query("SELECT w FROM Wishlist w WHERE w.member.memberCode = :memberCode AND w.product.id = :productId AND w.siteCd = :siteCd AND w.deleteYn = 'N'")
        Optional<Wishlist> findByMemberMemberCodeAndProduct_Id(@Param("memberCode") String memberCode,
                        @Param("productId") Long productId,
                        @Param("siteCd") String siteCd);

        @Query(value = "SELECT w FROM Wishlist w " +
                        "JOIN FETCH w.product p " +
                        "LEFT JOIN FETCH ProductSite ps ON p.id = ps.product.id " +
                        "LEFT JOIN FETCH FileStore fs ON CAST(p.id AS string) = fs.referenceId AND fs.referenceType = 'PRODUCT' AND fs.isMain = 'Y' AND fs.deleteYn = 'N' "
                        +
                        "WHERE w.member.memberCode = :memberCode AND w.siteCd = :siteCd AND w.deleteYn = 'N' ", countQuery = "SELECT COUNT(w) FROM Wishlist w WHERE w.member.memberCode = :memberCode AND w.siteCd = :siteCd AND w.deleteYn = 'N'")
        Page<Wishlist> findWishlistWithDetailsByMemberCode(@Param("memberCode") String memberCode, @Param("siteCd") String siteCd, Pageable pageable);

        @Query("SELECT COUNT(w) > 0 FROM Wishlist w WHERE w.member.memberCode = :memberCode AND w.product.id = :productId AND w.siteCd = :siteCd AND w.deleteYn = 'Y'")
        boolean existsDeletedByMemberAndProduct(@Param("memberCode") String memberCode,
                        @Param("productId") Long productId,
                        @Param("siteCd") String siteCd);

        @org.springframework.data.jpa.repository.Modifying
        @Query("UPDATE Wishlist w SET w.deleteYn = 'N', w.updatedAt = CURRENT_TIMESTAMP WHERE w.member.memberCode = :memberCode AND w.product.id = :productId AND w.siteCd = :siteCd AND w.deleteYn = 'Y'")
        void restoreWishlist(@Param("memberCode") String memberCode, @Param("productId") Long productId, @Param("siteCd") String siteCd);
}

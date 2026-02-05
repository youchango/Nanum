package com.nanum.domain.wishlist.repository;

import com.nanum.domain.wishlist.model.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByMemberMemberCodeAndProductProductId(String memberCode, Long productId);

    Optional<Wishlist> findByMemberMemberCodeAndProductProductId(String memberCode, Long productId);

    Page<Wishlist> findAllByMemberMemberCode(String memberCode, Pageable pageable);

    void deleteByMemberMemberCodeAndProductProductId(String memberCode, Long productId);
}

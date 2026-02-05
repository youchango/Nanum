package com.nanum.domain.cart.repository;

import com.nanum.domain.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberMemberCodeAndProductProductIdAndOptionId(String memberCode, Long productId,
            Long optionId);
}

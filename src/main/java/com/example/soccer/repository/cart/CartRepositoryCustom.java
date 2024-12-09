package com.example.soccer.repository.cart;

import com.example.soccer.domain.shop.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepositoryCustom {
    List<Cart> findCartDetails(Long memberId, Optional<Long> itemId);
}

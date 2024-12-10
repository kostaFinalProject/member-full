package com.example.soccer.repository.cart;

import com.example.soccer.domain.shop.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepositoryCustom extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.member.id = :memberId AND (:itemId IS NULL OR c.item.id = :itemId)")
    List<Cart> findCartDetails(Long memberId, Optional<Long> itemId);
//    @Query("SELECT c FROM Cart c JOIN FETCH c.items i WHERE c.id = :cartId AND (:optionalFilter IS NULL OR i.someField = :optionalFilter)")
//    List<Cart> findCartDetails(@Param("cartId") Long cartId, @Param("optionalFilter") Optional<Long> optionalFilter);
}

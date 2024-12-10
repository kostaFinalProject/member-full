package com.example.soccer.repository.cart;

import com.example.soccer.domain.shop.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    // CartId 리스트에 해당하는 장바구니 항목 모두 삭제 > 이건 front쪽에서 구현이 가능하지 않을까?
    void deleteAllByMemberIdAndItemIdIn(Long memberId, List<Long> itemIds);

    // CartId 리스트에 해당하는 장바구니 항목을 삭제
    void deleteAllByMemberIdAndIdIn(Long memberId, List<Long> cartItemIds);
}

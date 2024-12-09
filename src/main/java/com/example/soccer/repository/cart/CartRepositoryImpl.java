//package com.example.soccer.repository.cart;
//
//import com.example.soccer.domain.shop.Cart;
//import lombok.RequiredArgsConstructor;
//
//import com.querydsl.jpa.impl.JPAQuery;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//public class CartRepositoryImpl implements CartRepositoryCustom{
//
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public List<Cart> findCartDetails(Long memberId, Optional<Long> itemId) {
//        QCart cart = QCart.cart;
//        QMember member = QMember.member;
//        QItem item = QItem.item;
//
//        JPAQuery<Cart> query = queryFactory
//                .selectFrom(cart)
//                .join(cart.member, member).fetchJoin()
//                .join(cart.item, item).fetchJoin()
//                .where(cart.member.id.eq(memberId));
//
//        itemId.ifPresent(id -> query.where(cart.item.id.eq(id)));
//
//        return query.fetch();
//    }
//}

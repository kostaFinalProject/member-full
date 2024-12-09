//package com.example.soccer.service;
//
//import com.example.soccer.domain.Member;
//import com.example.soccer.domain.shop.Cart;
//import com.example.soccer.domain.shop.Item;
//import com.example.soccer.dto.cart.CartItemDto;
//import com.example.soccer.dto.cart.CartRequestDto;
//import com.example.soccer.dto.cart.CartResponseDto;
//import com.example.soccer.repository.cart.CartRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CartService {
//
//    private final CartRepository cartRepository;
//    private final EntityValidationService entityValidationService;
//
//    @Transactional
//    public boolean addCart(Long memberId, CartRequestDto cartRequestDto) {
//        Member member = entityValidationService.validateMember(memberId);
//        Item item = entityValidationService.validateItem(cartRequestDto.getItemId());
//
//        List<Cart> carts = cartRepository.findCartDetails(member.getId(), Optional.of(item.getId()));
//        Cart existingCart = carts.isEmpty() ? null : carts.get(0);
//
//        if (existingCart != null) {
//            existingCart.updateCount(existingCart.getCount() + cartRequestDto.getCount());
//            return false;
//        } else {
//            Cart newCart = Cart.createCart(member, item, cartRequestDto.getCount());
//            cartRepository.save(newCart);
//            return true;
//        }
//    }
//
//    @Transactional
//    public void updateCartItem(Long memberId, Long cartItemId, int newCount) {
//        Cart cart = entityValidationService.validateCart(cartItemId);
//
//        // 사용자 인증
//        if (!cart.getMember().getId().equals(memberId)) {
//            throw new IllegalStateException("해당 장바구니 항목에 접근할 권한이 없습니다.");
//        }
//
//        // 수량 업데이트
//        cart.updateCount(newCount);
//    }
//
//    @Transactional
//    public void deleteCartItems(Long memberId, List<Long> cartItemIds) {
//        if (cartItemIds.isEmpty()) {
//            throw new IllegalArgumentException("아무 상품도 선택되지 않았습니다.");
//        }
//        cartRepository.deleteAllByMemberIdAndIdIn(memberId, cartItemIds);
//    }
//
//    @Transactional(readOnly = true)
//    public CartResponseDto getCartItems(Long memberId) {
//        List<Cart> carts = cartRepository.findCartDetails(memberId, Optional.empty());
//        List<CartItemDto> cartItemDtoList = carts.stream()
//                .map(cart -> CartItemDto.createCartItemDto(cart.getId(), cart.getItem(), cart.getCount()))
//                .collect(Collectors.toList());
//
//        return CartResponseDto.createCartResponseDto(cartItemDtoList);
//    }
//}

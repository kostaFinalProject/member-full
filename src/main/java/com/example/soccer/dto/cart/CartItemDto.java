//package com.example.soccer.dto.cart;
//
//import com.example.soccer.domain.shop.Item;
//import com.example.soccer.domain.shop.ItemImg;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class CartItemDto {
//    private Long cartId;
//    private Long itemId;
//    private String name;
//    private int price;
//    private String repImgUrl;
//    private int count;
//    private int currentPrice; // 현재 가격 (price * count)
//
//    @Builder
//    private CartItemDto(Long cartId, Long itemId, String name, int price, String repImgUrl, int count, int currentPrice) {
//        this.cartId = cartId;
//        this.itemId = itemId;
//        this.name = name;
//        this.price = price;
//        this.repImgUrl = repImgUrl;
//        this.count = count;
//        this.currentPrice = currentPrice;
//    }
//
//    public static CartItemDto createCartItemDto(Long cartId, Item item, int count) {
//        String repImgUrl = item.getImages().stream()
//                .filter(ItemImg::isRepImgYn)
//                .map(ItemImg::getImgUrl)
//                .findFirst()
//                .orElse(null);
//
//        return CartItemDto.builder()
//                .cartId(cartId)
//                .itemId(item.getId())
//                .name(item.getName())
//                .price(item.getPrice())
//                .repImgUrl(repImgUrl)
//                .count(count)
//                .currentPrice(item.getPrice() * count)
//                .build();
//    }
//}
//

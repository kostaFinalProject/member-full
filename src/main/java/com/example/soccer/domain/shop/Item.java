package com.example.soccer.domain.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** 상품 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    private ItemCategory itemCategory;

    private String manufacturer;
    private String name;
    private int price;
    private int allStockQuantity;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSize> itemSizes = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImg> itemImages = new ArrayList<>();

//    @Builder
//    private Item(ItemCategory itemCategory, String manufacturer, String name, int price,
//                 List<ItemSize> itemSizes, List<ItemImg> itemImages) {
//        this.itemCategory = itemCategory;
//        this.manufacturer = manufacturer;
//        this.name = name;
//        this.price = price;
//        this.itemSizes = itemSizes;
//        this.itemImages = itemImages;
//        this.itemStatus = ItemStatus.ACTIVE;
//    }
//
//    public static Item createItem(ItemCategory itemCategory, String manufacturer, String name,
//                                  int price, List<ItemSize> itemSizes, List<ItemImg> itemImages) {
//        Item item = Item.builder().itemCategory(itemCategory).manufacturer(manufacturer).name(name)
//                .price(price).itemSizes(itemSizes).itemImages(itemImages).build();
//
//        for (ItemSize itemSize : itemSizes) {
//            itemSize.setItem(item);
//        }
//
//        for (ItemImg itemImg : itemImages) {
//            itemImg.setItem(item);
//        }
//
//        item.setAllStockQuantity();
//
//        return item;
//    }
//
//    public void updateItem(ItemCategory itemCategory, String name, int price, String manufacturer) {
//        this.itemCategory = itemCategory;
//        this.manufacturer = manufacturer;
//        this.name = name;
//        this.price = price;
//    }
//
//    /** 상품 이미지 수정 */
//    private void removeItemImg(ItemImg itemImg) {
//        if (this.itemImages.contains(itemImg)) {
//            this.itemImages.remove(itemImg);
//            itemImg.setItem(null);
//        }
//    }
//
//    /** 상품 이미지 수정 */
//    public void updateItemImg(List<ItemImg> newItemImages) {
//        for (ItemImg itemImg : new ArrayList<>(this.itemImages)) {
//            removeItemImg(itemImg);
//        }
//
//        for (ItemImg itemImg : newItemImages) {
//            itemImg.setItem(this);
//        }
//    }
//
//    protected void setAllStockQuantity() {
//        this.allStockQuantity = getAllStockQuantity();
//        if (this.allStockQuantity == 0) {
//            this.itemStatus = ItemStatus.SOLD_OUT;
//        }
//    }
//
//    /** 모든 사이즈의 재고를 합한 총 재고 수량 */
//    public int getAllStockQuantity() {
//        return itemSizes.stream()
//                .mapToInt(ItemSize::getStockQuantity)
//                .sum();
//    }
//
//    public ItemSize getItemSizeBySize(String size) {
//        return itemSizes.stream()
//                .filter(itemSize -> itemSize.getSize().getSize().equals(size))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("해당 사이즈의 상품이 존재하지 않습니다."));
//    }
//
//    /** 상품 활성화 */
//    public void activeItem() {
//        this.itemStatus = ItemStatus.ACTIVE;
//    }
//
//    /** 상품 비활성화 */
//    public void inActiveItem() {
//        this.itemStatus = ItemStatus.INACTIVE;
//    }
//
//    /** 상품 삭제 (실제 DB 삭제 X) Soft Delete */
//    public void deleteItem() {
//        this.itemStatus = ItemStatus.DELETED;
//    }
//
//    /** 상품의 대표 이미지 가져오기 */
//    public String getRepItemImage() {
//        return this.itemImages.stream()
//                .filter(ItemImg::isRepImgYn)
//                .findFirst()
//                .map(ItemImg::getImgUrl)
//                .orElse(getItemImages().get(0).getImgUrl());
//    }
}

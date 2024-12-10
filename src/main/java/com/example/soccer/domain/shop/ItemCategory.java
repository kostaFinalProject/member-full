package com.example.soccer.domain.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 카테고리
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_category_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_item_category_id")
    private ItemCategory parentItemCategory;

    @OneToMany(mappedBy = "parentItemCategory", cascade = CascadeType.ALL)
    private List<ItemCategory> childrenItemCategories = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_category_img_id")
    private ItemCategoryImg itemCategoryImg;

    @Builder
    private ItemCategory(String name, ItemCategory parentItemCategory, ItemCategoryImg itemCategoryImg) {
        this.name = name;
        this.parentItemCategory = parentItemCategory;
        this.itemCategoryImg = itemCategoryImg;
    }

    public static ItemCategory createItemCategory(String name, ItemCategory parentItemCategory, ItemCategoryImg itemCategoryImg) {
        ItemCategory itemCategory = ItemCategory.builder()
                .name(name)
                .parentItemCategory(parentItemCategory)
                .itemCategoryImg(itemCategoryImg)
                .build();

        if (parentItemCategory != null) {
            parentItemCategory.addChildItemCategory(itemCategory);
        }

        return itemCategory;
    }

    /** 부모-자식 관계 관리 메서드 */
    protected void addChildItemCategory(ItemCategory childItemCategory) {
        this.childrenItemCategories.add(childItemCategory);
        childItemCategory.setParentItemCategory(this);
    }

    protected void setParentItemCategory(ItemCategory parentItemCategory) {
        this.parentItemCategory = parentItemCategory;
    }

    /** 카테고리 업데이트 */
    public void updateItemCategory(String name, ItemCategory parentItemCategory, ItemCategoryImg itemCategoryImg) {
        this.name = name;

        if (this.parentItemCategory != null && this.parentItemCategory != parentItemCategory) {
            this.parentItemCategory.getChildrenItemCategories().remove(this);
        }
        if (this.parentItemCategory != parentItemCategory) {
            this.parentItemCategory = parentItemCategory;
            if (parentItemCategory != null) {
                parentItemCategory.addChildItemCategory(this);
            }
        }

        this.itemCategoryImg = itemCategoryImg;
    }
}

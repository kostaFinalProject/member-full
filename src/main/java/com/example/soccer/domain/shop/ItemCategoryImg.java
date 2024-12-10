package com.example.soccer.domain.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemCategoryImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_category_img_id")
    private Long id;

    private String imgName;
    private String originImgName;
    private String imgUrl;

    @Builder
    private ItemCategoryImg(String imgName, String originImgName, String imgUrl) {
        this.imgName = imgName;
        this.originImgName = originImgName;
        this.imgUrl = imgUrl;
    }

    public static ItemCategoryImg createItemCategoryImg(String imgName, String originImgName, String imgUrl) {
        return ItemCategoryImg.builder().imgName(imgName).originImgName(originImgName).imgUrl(imgUrl).build();
    }
}

package com.example.soccer.domain.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 상품 이미지 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImg {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id")
    private Long id;

    private String imgName;
    private String originImgName;
    private String imgUrl;
    private String repImgYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

//    @Builder
//    private ItemImg(String imgName, String originImgName, String imgUrl, String repImgYn) {
//        this.imgName = imgName;
//        this.originImgName = originImgName;
//        this.imgUrl = imgUrl;
//        this.repImgYn = repImgYn;
//    }
//
//    /** 연관 관계 메서드 */
//    protected void setItem(Item item) {
//        if (this.item != null) {
//            this.item.getItemImages().remove(this);
//        }
//
//        this.item = item;
//        if (this.item != null && !this.item.getItemImages().contains(this)) {
//            this.item.getItemImages().add(this);
//        }
//    }
//
//    public static ItemImg createItemImg(String imgName, String originImgName,
//                                        String imgUrl, String repImgYn) {
//        return ItemImg.builder()
//                .imgName(imgName)
//                .originImgName(originImgName)
//                .imgUrl(imgUrl)
//                .repImgYn(repImgYn)
//                .build();
//    }
//
//    /**
//     * 대표 이미지 여부 확인 메서드
//     */
//    public boolean isRepImgYn() {
//        return "Y".equals(this.repImgYn);
//    }
}

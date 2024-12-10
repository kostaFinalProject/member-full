package com.example.soccer.repository.item;

import com.example.soccer.domain.shop.Item;
import com.example.soccer.domain.shop.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    /** 특정 Item에 속한 모든 이미지 조회 */
    @Query("SELECT img FROM ItemImg img WHERE img.item = :item")
    List<ItemImg> findByItemWithImages(@Param("item") Item item);

    /** 특정 Item의 대표 이미지 조회 */
    @Query("SELECT img FROM ItemImg img WHERE img.item = :item AND img.repImgYn = 'Y'")
    Optional<ItemImg> findRepresentativeImage(@Param("item") Item item);
}

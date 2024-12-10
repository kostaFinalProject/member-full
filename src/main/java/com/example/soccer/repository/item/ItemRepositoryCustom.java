package com.example.soccer.repository.item;

import com.example.soccer.domain.shop.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> searchItems(String keyword, String category, Pageable pageable);
}

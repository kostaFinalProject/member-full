package com.example.soccer.domain;

import jakarta.persistence.*;

public class Item {

    @Id @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="category_id")
    private String categoryId;

    private String name;

    private String explanation;

    private String price;

    private String dtype;
}

package com.example.soccer.domain.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사이즈
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Size {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Long id;
    private String size;

    @Builder
    private Size(String size) {
        this.size = size;
    }

    /** 테스트 전용으로 생성한 사이즈 생성자 메서드 (실제 사이즈는 XS, S, M, L, XL, 2XL 6가지로 고정) */
    public static Size createSize(String size) {
        return Size.builder().size(size).build();
    }
}

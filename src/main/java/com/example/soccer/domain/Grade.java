package com.example.soccer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade { // 인덱스
    SUPER_ADMIN("GRADE_SUPER_ADMIN"),
    ADMIN("ADMIN"),
    USER("USER");

    private final String key;
}

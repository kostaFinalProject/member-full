package com.example.soccer.dto.login;

import jakarta.validation.constraints.NotBlank;
// record는 불변 객체를 쉽게 만들 수 있게 해주는 구조
/** Auth DTO */
public record LoginResponse(@NotBlank String accessToken) {
}

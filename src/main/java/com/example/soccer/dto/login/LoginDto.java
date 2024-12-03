package com.example.soccer.dto.login;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // jakarta.validation. 사용 여부
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class LoginDto {
    private String userId;
    private String password;

    @Builder
    private LoginDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}

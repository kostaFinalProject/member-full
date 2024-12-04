package com.example.soccer.dto.login;

import com.example.soccer.domain.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthConverter {

    public static Member toMember(String email, String name, String password, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
    }
}

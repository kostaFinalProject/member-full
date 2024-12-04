package com.example.soccer.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class KakaoMemberDetails implements OAuth2User { // 로그인 성공 후처리 서비스를 구현하기에 앞서 후처리 서비스에서 필요한 정보들을 먼저 구현

    private final String email;
    private final List<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

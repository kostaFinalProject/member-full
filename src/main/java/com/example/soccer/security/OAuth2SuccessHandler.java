package com.example.soccer.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
//    private final TokenProvider tokenProvider;
    private static final String URI = "http://127.0.0.1:5500/main.html";
    // OAuth2 인증이 성공한 후 사용자를 리다이렉트할 클라이언트 애플리케이션(프론트엔드)의 URL

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // authentication에서 userId를 추출
        String userId = authentication.getName();  // 일반적으로 getName()으로 userId를 가져옵니다.

        // accessToken, refreshToken 발급 (userId를 전달)
        String accessToken = jwtUtil.generateAccessToken(userId); // userId를 전달하여 토큰 생성
//        String refreshToken = jwtUtil.generateRefreshToken(userId);  // userId를 전달하여 refreshToken 생성
        jwtUtil.generateRefreshToken(userId, accessToken);

        // 토큰 전달을 위한 redirect
        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", accessToken)
//                .queryParam("refreshToken", refreshToken)  // refreshToken도 전달
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
    // Authentication 객체 사용 방법
    // Principal: 사용자의 기본 정보(예: username, userId 등)
    // Authorities: 사용자가 가진 권한 정보
    // Credentials: 사용자의 인증 정보 (예: 비밀번호)
    // Details: 추가적인 세부 정보 (예: IP 주소)
}

package com.example.soccer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String path = request.getRequestURI();
        log.debug("Request Path: {}", path);

//        // 회원가입 및 로그인 API에 대해서는 JWT 검증을 우회 - front 403 error로 추가했으나 없어도 된다
//        if (path.equals("/api/members/signup") || path.equals("/api/members/login")) {
//            log.debug("Skipping JWT validation for path: {}", path);
//            chain.doFilter(request, response); // 필터를 계속해서 실행
//            return;
//        }

        // Refresh Token 처리 로직
        if ("/api/members/refresh-token".equals(path)) {
            String refreshToken = request.getHeader("Refresh-Token");
            if (refreshToken == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Refresh Token");
                return;
            }
            String jwtRefreshToken = refreshToken.replace("Bearer ", "");
            log.debug("Refresh-Token Header: {}", jwtRefreshToken);

            if (jwtUtil.validateRefreshToken(jwtRefreshToken)) {
                log.debug("Refresh token validated successfully");
                chain.doFilter(request, response);
                return;
            } else {
                log.debug("Invalid refresh token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Refresh Token");
                return;
            }
        }

        // Authorization 헤더에서 JWT 토큰 추출
        String header = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", header);

        // SecurityContext에 인증이 이미 존재하는지 확인
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7); // "Bearer " 제거
            log.debug("Extracted Token: {}", token);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                var userDetails = customUserDetailsService.loadUserByUsername(username);
                log.debug("User Details Loaded: {}", userDetails.getUsername());

                // 인증 토큰 생성 및 SecurityContext 설정
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set in SecurityContext");
            } else {
                log.debug("Invalid JWT Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        } else {
            log.debug("No Authorization Header or authentication already set");
        }

        chain.doFilter(request, response);
    }
    // userId로 받을 때
    private void setAuthentication(String email) {
        // userId를 이용해 Authentication 객체를 생성 후 SecurityContext에 설정
        var userDetails = customUserDetailsService.loadUserByUsername(email);
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 토큰을 추출하는 부분도 필요에 따라 수정 가능
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7); // "Bearer "를 제거하고 반환
    }
    // 객체로 받을 때
//    private void setAuthentication(String accessToken) {
//        Authentication authentication = jwtUtil.getAuthentication(accessToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String token = request.getHeader(AUTHORIZATION);
//        if (ObjectUtils.isEmpty(token) || !token.startsWith(TokenKey.TOKEN_PREFIX)) {
//            return null;
//        }
//        return token.substring(TokenKey.TOKEN_PREFIX.length());
//    }
}

//package com.example.soccer.security;
//
//import com.example.soccer.service.TokenService;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtUtil1 { // > 이 파일의 객체 타입을 스트링에서 객체로 바꾸려다 실패
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final TokenService tokenService;
//
//    @Value("${spring.jwt.secret-key}") // properties 또는 환경 변수에서 값을 가져올 수 있으며, 두 곳에 모두 값이 있다면, 환경 변수가 yml/properties 파일보다 우선시
//    private String encodedSecretKey;
//
//    @PostConstruct
//    public void checkSecretKey() {
//        if (encodedSecretKey == null || encodedSecretKey.isEmpty()) {
//            throw new IllegalArgumentException("Secret Key is missing in application.properties or environment variables.");
//        }
//        System.out.println("Loaded Secret Key: " + encodedSecretKey);
//    }
//
//    // 인증키가 디코더 변환이 필요할 때
//    private SecretKey getSecretKey() {
//        byte[] keyBytes = Base64.getDecoder().decode(encodedSecretKey);
//        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
//    }
//
//    // Access Token 발급 (기본 만료 시간: 15분)
//    public String generateToken(Authentication authentication) {
//        String userId = authentication.getName(); // Authentication 객체에서 userId를 가져옴
//        return generateToken(userId, 1000 * 60 * 15);
//    }
//
//    // Refresh Token 발급 (기본 만료 시간: 7일)
//    public String generateRefreshToken(Authentication authentication) {
//        String userId = authentication.getName(); // Authentication 객체에서 userId를 가져옴
//        String refreshToken = generateToken(userId, 1000 * 60 * 60 * 24 * 7);
//        saveRefreshToken(userId, refreshToken);
//        return refreshToken;
//    }
//
//    // JWT Token 생성
//    private String generateToken(String userId, long expiration) {
//        return Jwts.builder()
//                .setSubject(userId)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSecretKey())
//                .compact();
//    }
//
//    // 토큰 검증 (Blacklisted 확인 포함)
//    public boolean validateToken(String token) {
//        try {
//            if (isTokenBlacklisted(token)) {
//                log.debug("Token is blacklisted: {}", token);
//                return false;
//            }
//            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            log.debug("Error validating JWT Token: {}", token, e);
//            return false;
//        }
//    }
//
//    // BlackList 추가 (logout 시 호출)
//    public void invalidateToken(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
//            long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
//            redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS);
//            log.debug("Token invalidated and blacklisted: {}", token);
//        } catch (Exception e) {
//            log.error("Error invalidating token: {}", token, e);
//        }
//    }
//
//    // 토큰에서 사용자 정보 (userId) 추출
//    public String extractUsername(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(getSecretKey())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getSubject();
//        } catch (Exception e) {
//            log.error("Error extracting username from token: {}", token, e);
//            return null;
//        }
//    }
//
//    // 토큰이 BlackList에 있는지 확인
//    private boolean isTokenBlacklisted(String token) {
//        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(token));
//        log.debug("Token blacklist check: {}, Result: {}", token, isBlacklisted);
//        return isBlacklisted;
//    }
//
//    // Refresh Token 저장 (Redis)
//    public void saveRefreshToken(String userId, String refreshToken) {
//        try {
//            redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken, 7, TimeUnit.DAYS);
//            log.debug("Refresh token saved for userId: {}", userId);
//        } catch (Exception e) {
//            log.error("Error saving refresh token for userId: {}", userId, e);
//        }
//    }
//
//    // Refresh Token 유효성 확인
//    public boolean validateRefreshToken(String refreshToken) {
//        try {
//            Claims claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(refreshToken).getBody();
//            String userId = claims.getSubject();
//            String storedRefreshToken = (String) redisTemplate.opsForValue().get("refreshToken:" + userId);
//
//            log.debug("Refresh Token from Redis: {}", storedRefreshToken);
//            log.debug("Refresh Token from Request: {}", refreshToken);
//
//            boolean isValid = refreshToken.equals(storedRefreshToken);
//            log.debug("Refresh Token Validation Result: {}", isValid);
//            return isValid;
//        } catch (Exception e) {
//            log.debug("Error validating Refresh Token", e);
//            return false;
//        }
//    }
//
//    // Refresh Token 재생성 및 저장 (기존 토큰 무효화)
//    public String regenerateRefreshToken(Authentication authentication) {
//        String userId = authentication.getName(); // Authentication 객체에서 userId를 가져옴
//        try {
//            redisTemplate.delete("refreshToken:" + userId); // 기존 Refresh Token 삭제
//            log.debug("Old refresh token deleted for userId: {}", userId);
//            String newRefreshToken = generateRefreshToken(authentication); // 새로운 Refresh Token 발급
//            saveRefreshToken(userId, newRefreshToken);
//            log.debug("New Refresh Token Generated: {}", newRefreshToken);
//            return newRefreshToken;
//        } catch (Exception e) {
//            log.error("Error regenerating refresh token for userId: {}", userId, e);
//            return null;
//        }
//    }
//
//    // 새로운 Access Token 발급 (Refresh Token 사용)
//    public String refreshAccessToken(String refreshToken) {
//        if (validateRefreshToken(refreshToken)) {
//            String userId = extractUsername(refreshToken);
//            return generateToken(userId); // 새로운 Access Token 발급
//        } else {
//            throw new IllegalArgumentException("Invalid refresh token");
//        }
//    }
//}

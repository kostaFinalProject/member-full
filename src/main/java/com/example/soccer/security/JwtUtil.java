package com.example.soccer.security;

import com.example.soccer.domain.Grade;
import com.example.soccer.domain.Token;
import com.example.soccer.exception.TokenException;
import static com.example.soccer.exception.ErrorCode.INVALID_TOKEN;
import static com.example.soccer.exception.ErrorCode.INVALID_JWT_SIGNATURE;
import com.example.soccer.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

//    private final JwtProperties jwtProperties;
//    private String secretKey = "default-secret-key"; // 임시 비밀키
    private final TokenService tokenService; // > 순환의존성 걸렸음
//    private static final String KEY_ROLE = "role"; // 이거 대신 Grade key로 사용

    /** 시크릿키 */
    @Value("${spring.jwt.secret-key}") // properties 또는 환경 변수에서 값을 가져올 수 있으며, 두 곳에 모두 값이 있다면, 환경 변수가 yml/properties 파일보다 우선시
    private String encodedSecretKey; // 이거 입력하면 계속 충돌남 > 이미 생성되어 있어서 그런듯

    @PostConstruct
    public void checkSecretKey() {
        if (encodedSecretKey == null || encodedSecretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret Key is missing in application.properties or environment variables.");
        }
        System.out.println("Loaded Secret Key: " + encodedSecretKey);
    }

    // Base64로 인코딩이 안된 16진수 문자열을 바이트 배열로 전환 - getSecretKey 방식
    private SecretKey getSecretKey() {
////        String encodedSecretKey= jwtProperties.getSecretKey();
//        if (encodedSecretKey == null || encodedSecretKey.isEmpty()) {
//            throw new IllegalArgumentException("Secret Key is missing.");
//        }
        byte[] keyBytes = Base64.getDecoder().decode(encodedSecretKey);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
    ////     비밀키가 문자열일 때
//    public SecretKey getSecretKey() {
//        String secretKeyString = "your-secret-key";  // application.yml에서 가져온 비밀 키
//        return new SecretKeySpec(secretKeyString.getBytes(), SignatureAlgorithm.HS256.getJcaName());
//    }

    // Base64로 인코딩된 문자열 - setSecretKey 방식
//    @PostConstruct
//    private void setSecretKey() {
//        secretKey = Keys.hmacShaKeyFor(encodedSecretKey.getBytes());
//    }

    /** 레디스 템플릿 */
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void testRedis() {
        redisTemplate.opsForValue().set("testKey", "testValue", 10, TimeUnit.SECONDS);
        System.out.println("Test Redis Value: " + redisTemplate.opsForValue().get("testKey"));
    }


    // 일반 로그인 :  Access Token 발급 (기본 만료 시간: 15분)
    public String generateAccessToken(String userId) {
        return generateToken(userId, 1000 * 60 * 15);
    }

    // Refresh Token 발급 (기본 만료 시간: 7일)
    public String generateRefreshToken(String userId, String accessToken) {
        String refreshToken = generateToken(userId, 1000 * 60 * 60 * 24 * 7);
        tokenService.saveOrUpdate(userId, refreshToken, accessToken);
        return refreshToken;
    }

    // JWT Token 생성 > 이거 퍼블릭해도 되나
    public String generateToken (String userId, long expireTime) {
        // 권한 정보 추출 > private String generateToken(String userId, long expireTime) { 이 방식으로는 불가
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining());
        // 사용자 권한 가져오기
//        Grade userGrade = getUserGrade(userId); // 사용자 ID를 기반으로 권한 조회
//        private Grade getUserGrade(String userId) {
//            // 예: 데이터베이스에서 사용자 권한 조회
//            if ("adminUser".equals(userId)) {
//                return Grade.ADMIN;
//            } else if ("superAdminUser".equals(userId)) {
//                return Grade.SUPER_ADMIN;
//            }
//            return Grade.USER; // 기본 권한
//        }
        return Jwts.builder()
                .setSubject(userId) // 사용자 ID를 subject로 설정
                .claim("role", Grade.USER.getKey())  // 권한 정보 (role)을 'claim'으로 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // 현재 시간을 발급일시로 설정
                .setExpiration(new Date(System.currentTimeMillis() + expireTime)) // 만료 시간을 설정
                .signWith(getSecretKey()) // 비밀키로 서명
                .compact(); // 최종적으로 JWT 토큰 생성
    }

    // 토큰 검증 (Blacklisted 확인 포함) - 블랙리스트 확인, 서명 검증, 그리고 예외 처리까지 포함한 종합적인 토큰 검증
    public boolean validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                log.debug("Token is blacklisted: {}", token);
                return false;
            }
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("Error validating JWT Token: {}", token, e);
            return false;
        }
    }

    // BlackList 추가 (logout 시 호출)
    public void invalidateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody(); // JWT 파싱
            long expiration = claims.getExpiration().getTime() - System.currentTimeMillis(); // 만료 시간 계산
            redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS); // Redis에 블랙리스트 추가            log.debug("Token invalidated and blacklisted: {}", token);
        } catch (Exception e) {
            log.error("Error invalidating token: {}", token, e);
        }
    }

    // 토큰이 BlackList에 있는지 확인
    private boolean isTokenBlacklisted(String token) {
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(token));
        log.debug("Token blacklist check: {}, Result: {}", token, isBlacklisted);
        return isBlacklisted;
    }





    // 토큰에서 이메일(사용자 정보) 추출
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", token, e);
            return null;
        }
    }

    // Refresh Token 유효성 확인
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(refreshToken).getBody();
            String userId = claims.getSubject();
            String storedRefreshToken = (String) redisTemplate.opsForValue().get("refreshToken:" + userId);

            log.debug("Refresh Token from Redis: {}", storedRefreshToken);
            log.debug("Refresh Token from Request: {}", refreshToken);

            boolean isValid = refreshToken.equals(storedRefreshToken);
            log.debug("Refresh Token Validation Result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.debug("Error validating Refresh Token", e);
            return false;
        }
    }


    // Refresh Token 재생성 및 저장 (기존 토큰 무효화)
    public String regenerateRefreshToken(String userId) {
        try {
            // 기존 Refresh Token 삭제
            redisTemplate.delete("refreshToken:" + userId); // 기존 Refresh Token 삭제
            log.debug("Old refresh token deleted for userId: {}", userId);

            // 새로운 Access Token 생성
            String newAccessToken = generateAccessToken(userId);  // 새로 발급된 Access Token 생성

            // 새로운 Refresh Token 생성 및 저장
            String newRefreshToken = generateRefreshToken(userId, newAccessToken);
            tokenService.saveOrUpdate(userId, newRefreshToken, newAccessToken);
//            saveRefreshToken(userId, newRefreshToken);
            log.debug("New Refresh Token Generated: {}", newRefreshToken);
            return newRefreshToken;
        } catch (Exception e) {
            log.error("Error regenerating refresh token for userId: {}", userId, e);
            return null;
        }
    }

    // 새로운 Access Token 발급 (Refresh Token 사용)
    public String refreshAccessToken(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String userId = extractUsername(refreshToken);
            long expireTime = 1000 * 60 * 15; // 예: 1시간 (밀리초 단위로 설정)
            return generateToken(userId, expireTime); // 새로운 Access Token 발급
        } else {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    // 액세스 토큰을 갱신(reissue)하는 메서드 (Access Token 사용)
    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            if (validateToken(refreshToken)) {
                // refreshToken을 사용해 새로운 AccessToken을 발급
                Authentication authentication = getAuthentication(refreshToken);
                String userId = authentication.getName(); // 사용자 ID 추출
                String reissueAccessToken = generateAccessToken(userId);
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }



    // 사용자 인증(Authentication) 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // claims에서 사용자 권한을 추출하여 SimpleGrantedAuthority 객체로 반환
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role", String.class)));
    }

    private Claims parseClaims(String token) {
        try {
//            return Jwts.parser().verifyWith(getSecretKey).build()
//                    .parseSignedClaims(token).getPayload();
            return Jwts.parserBuilder().setSigningKey(getSecretKey()).build()
                    .parseClaimsJws(token).getBody(); // 서명 검증을 위해 getSecretKey() 사용
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new TokenException(INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(INVALID_JWT_SIGNATURE);
        }
    }
}

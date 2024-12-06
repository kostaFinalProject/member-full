package com.example.soccer.service;

import com.example.soccer.domain.Token;
import com.example.soccer.exception.TokenException;
import static com.example.soccer.exception.ErrorCode.TOKEN_EXPIRED;
import com.example.soccer.repository.TokenRepository;
import com.example.soccer.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository tokenRepository;

    /** 토근 저장 또는 업데이트? */
    public void saveOrUpdate(String memberKey, String refreshToken, String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .map(o -> o.updateRefreshToken(refreshToken))
                .orElseGet(() -> new Token(memberKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    /** accessToken 사용하여 사용자 조회 */
    public Token findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
    }

    /** 토근 갱신 */
    public void updateToken(String accessToken, Token token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }

    /** Refresh 토근 삭제 */
    public void deleteRefreshToken(String memberKey) {
        tokenRepository.deleteById(memberKey);
    }



//    /** 시크릿키 */
//    @Value("${spring.jwt.secret-key}") // properties 또는 환경 변수에서 값을 가져올 수 있으며, 두 곳에 모두 값이 있다면, 환경 변수가 yml/properties 파일보다 우선시
//    private String encodedSecretKey; // 이거 입력하면 계속 충돌남 > 이미 생성되어 있어서 그런듯
//
//    @PostConstruct
//    public void checkSecretKey() {
//        if (encodedSecretKey == null || encodedSecretKey.isEmpty()) {
//            throw new IllegalArgumentException("Secret Key is missing in application.properties or environment variables.");
//        }
//        System.out.println("Loaded Secret Key: " + encodedSecretKey);
//    }
//
//    // Base64로 인코딩이 안된 16진수 문자열을 바이트 배열로 전환 - getSecretKey 방식
//    private SecretKey getSecretKey() {
//        byte[] keyBytes = Base64.getDecoder().decode(encodedSecretKey);
//        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
//    }
//
//    // 일반 로그인 :  Access Token 발급 (기본 만료 시간: 15분)
//    public String generateAccessToken(String memberKey) {
//        return generateToken(memberKey, 1000 * 60 * 15);
//    }
//
//    public String generateToken(String memberKey, long expireTime) {
//        return Jwts.builder()
//                .setSubject(memberKey)  // memberKey를 subject로 설정
//                .setIssuedAt(new Date())  // 토큰 발급 시간
//                .setExpiration(new Date(System.currentTimeMillis() + expireTime))  // 만료 시간
//                .signWith(getSecretKey())  // 서명 알고리즘 및 비밀 키
//                .compact();  // 토큰 생성
//    }
}

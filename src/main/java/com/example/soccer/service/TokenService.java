package com.example.soccer.service;

import com.example.soccer.domain.Token;
import com.example.soccer.exception.TokenException;
import static com.example.soccer.exception.ErrorCode.TOKEN_EXPIRED;
import com.example.soccer.repository.TokenRepository;
import com.example.soccer.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository tokenRepository;

    /** 토근 삭제 */
    public void deleteRefreshToken(String memberKey) {
        tokenRepository.deleteById(memberKey);
    }

    /** 토근 저장 또는 업데이트? */
    public void saveOrUpdate(String memberKey, String refreshToken, String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .map(o -> o.updateRefreshToken(refreshToken))
                .orElseGet(() -> new Token(memberKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    public Token findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
    }

    /** 토근 업데이트? */
    public void updateToken(String accessToken, Token token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }

//    // 이 방법은JWT 생성 및 검증에 집중하며, 데이터베이스나 캐시와는 독립적으로 작동
    private final JwtUtil jwtUtil; // 이 부분에서 순환참조 오류 발생

    public String generateAccessToken(String email) {

        return jwtUtil.generateToken(email, 1000 * 60 * 15);
    }

    public String generateRefreshToken(String email, String accessToken) {

        return jwtUtil.generateRefreshToken(email, accessToken);
    }

    public boolean validateAccessToken(String token) {

        return jwtUtil.validateToken(token);
    }

    public void invalidateToken(String token) {
        jwtUtil.invalidateToken(token);
    }

    public String regenerateRefreshToken(String email) {

        return jwtUtil.regenerateRefreshToken(email);
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    public String refreshAccessToken(String refreshToken) {
        log.debug("Refreshing access token using refresh token: {}", refreshToken);
        return jwtUtil.refreshAccessToken(refreshToken);
    }
}

package com.example.soccer.controller;

import com.example.soccer.aop.TokenApi;
import com.example.soccer.dto.login.RefreshTokenResponse;
import com.example.soccer.dto.login.TokenValidationResponse;
import com.example.soccer.service.TokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    /** Token 인증 */
    @TokenApi
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        boolean isValid = tokenService.validateAccessToken(jwtToken);
        return ResponseEntity.ok(TokenValidationResponse.createTokenValidationResponse(isValid));
    }

    /** Refresh Token 재발급 */
    @TokenApi
    @PostMapping("/refresh")
    public ResponseEntity<?> regenerateRefreshToken(@RequestHeader("Authorization") String refreshToken) {
        String jwtRefreshToken = refreshToken.replace("Bearer ", "");
        String email = tokenService.extractUsername(jwtRefreshToken);
        String newRefreshToken = tokenService.regenerateRefreshToken(email);
        return ResponseEntity.ok(RefreshTokenResponse.createRefreshTokenResponse("Bearer " + newRefreshToken));
    }
}


package com.example.soccer.controller;

import com.example.soccer.dto.login.LoginResponse;
import com.example.soccer.service.RedisMessageService;
import com.example.soccer.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final TokenService tokenService;
    private final RedisMessageService redisMessageService;

    @GetMapping("/auth/success")
    public ResponseEntity<LoginResponse> loginSuccess(@Valid LoginResponse loginResponse) {
        return ResponseEntity.ok(loginResponse);
    }

//    @GetMapping("/auth/success")
//    public ResponseEntity<LoginResponse> loginSuccess(@AuthenticationPrincipal UserDetails userDetails, @Valid LoginResponse loginResponse) {
//        // 사용자의 username을 사용해 access_token을 생성
//        String accessToken = tokenService.generateAccessToken(userDetails.getUsername());
//        return ResponseEntity.ok(new LoginResponse(accessToken));
//    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        tokenService.deleteRefreshToken(userDetails.getUsername());
        redisMessageService.removeSubscribe(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
    // Access Token은 사용자가 인증을 받은 후 서버에서 발급
    // Refresh Token은 access token이 만료되었을 때, 새 access token을 발급받기 위한 토큰
}

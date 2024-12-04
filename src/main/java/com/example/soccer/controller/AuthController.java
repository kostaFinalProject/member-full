//package com.example.soccer.controller;
//
//import com.example.soccer.domain.Member;
//import com.example.soccer.service.AuthService;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("api/auth")
//public class AuthController {
//
//    private final AuthService authService; // 주입된 AuthService 인스턴스
//
////    @PostMapping("/login")
////    public ResponseEntity<?> join(@RequestBody MemberRequestDTO.LoginRequestDTO loginRequestDTO) {
////        return null;
////    }
//
//    @GetMapping("/login/kakao")
//    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
//        Member member = authService.oAuthLogin(accessCode, httpServletResponse);
////        return BaseResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
//        return ResponseEntity.ok("Login successful, token set in Authorization header");
//    }
//}

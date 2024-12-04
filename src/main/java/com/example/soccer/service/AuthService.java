//package com.example.soccer.service;
//
//import com.example.soccer.domain.Member;
//import com.example.soccer.dto.login.AuthConverter;
//import com.example.soccer.dto.login.KakaoDTO;
//import com.example.soccer.repository.MemberRepository;
//import com.example.soccer.security.JwtUtil;
//import com.example.soccer.security.KakaoUtil;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final KakaoUtil kakaoUtil;
//    private final MemberRepository memberRepository;
//    private final JwtUtil jwtUtil;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public Member oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
//        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
//        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
//        String email = kakaoProfile.getKakao_account().getEmail();
//
//        Member member = memberRepository.findByEmail(email)
//                .orElseGet(() -> createNewMember(kakaoProfile));
//
//        String token = jwtUtil.generateToken(member.getEmail());
//        httpServletResponse.setHeader("Authorization", token);
//
//        return member;
//    }
//
//    private Member createNewMember(KakaoDTO.KakaoProfile kakaoProfile) {
//        Member newMember = AuthConverter.toMember(
//                kakaoProfile.getKakao_account().getEmail(),
//                kakaoProfile.getKakao_account().getProfile().getNickname(),
//                null,
//                passwordEncoder
//        );
//        return memberRepository.save(newMember);
//    }
//}

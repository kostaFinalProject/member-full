//package com.example.soccer.security;
//
//import com.example.soccer.domain.Member;
//import com.example.soccer.dto.login.KakaoUserInfo;
//import com.example.soccer.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoMemberDetailsService extends DefaultOAuth2UserService {
//
//    private static final String PREFIX = "낯선 ";
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
//
//        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
//                .orElseGet(() ->
//                        memberRepository.save(
//                                Member.builder()
//                                        .email(kakaoUserInfo.getEmail())
//                                        .nickname(PREFIX)
//                                        .updateAgeCount(0)
//                                        .updateGenderCount(0)
//                                        .build()
//                        )
//                );
//
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole().name());
//
//        return new KakaoMemberDetails(String.valueOf(member.getEmail()),
//                Collections.singletonList(authority),
//                oAuth2User.getAttributes());
//    }
//}

//package com.example.soccer.service;
//
//import com.example.soccer.domain.Member;
//import com.example.soccer.repository.MemberRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
//
//        OAuth2User oAuth2User = super.loadUser(request);
//        String oauthClientName = request.getClientRegistration().getClientName();
//
//        try {
//            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//
//        Member member = null;
//        String userId = null;
//
//        if (oauthClientName.equals("kakao")){
//            userId="kakao_"+oAuth2User.getAttributes().get("id");
//            member = new Member(userId, "kakao");
//
//        }
//
//        if (oauthClientName.equals("naver")){
//
//        }
//
//        return oAuth2User;
//    }
//}

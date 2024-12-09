package com.example.soccer.security;

import com.example.soccer.domain.Member;
import com.example.soccer.dto.login.OAuth2UserInfo;
import com.example.soccer.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("OAuth2 로그인 요청: " + userRequest); // 디버그 출력
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        System.out.println("Attributes: " + oAuth2UserAttributes); // 응답 데이터 확인

        // 2. resistrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        Member member = getOrSave(oAuth2UserInfo);

        // 6. OAuth2User로 반환
        return new PrincipalDetails(member, oAuth2UserAttributes, userNameAttributeName);
    }
    private Member getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        System.out.println("OAuth2 User Info: " + oAuth2UserInfo);

        // 이메일로 이미 가입된 회원을 조회 (Optional로 반환)
        Optional<Member> memberOptional = memberRepository.findByEmail(oAuth2UserInfo.email());

        // 이미 가입된 회원이 있을 경우
        if (memberOptional.isPresent()) {
            // 이미 가입된 회원을 그대로 반환
            return memberOptional.get();
        }

        // 이미 가입된 회원이 없으면 새 회원 생성
        Member member = oAuth2UserInfo.toEntity();
        return memberRepository.save(member);
    }
}

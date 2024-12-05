package com.example.soccer.dto.login;



import com.example.soccer.domain.Grade;
import com.example.soccer.domain.Member;
import static com.example.soccer.exception.ErrorCode.ILLEGAL_REGISTRATION_ID;
import com.example.soccer.exception.AuthException;
import com.example.soccer.security.KeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
//        String name,
        String email
//        String profile
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
//            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

//    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
//        return OAuth2UserInfo.builder()
////                .name((String) attributes.get("name"))
//                .email((String) attributes.get("email"))
////                .profile((String) attributes.get("picture"))
//                .build();
//    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        // 제너릭 오류 발생
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
//        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

//        Object kakaoAccount = attributes.get("kakao_account");
//        if (kakaoAccount instanceof Map<?, ?>) {
//            Map<String, Object> account = (Map<String, Object>) kakaoAccount;
//            Object kakaoProfile = account.get("profile");
//            if (kakaoProfile instanceof Map<?, ?>) {
//                Map<String, Object> profile = (Map<String, Object>) kakaoProfile;
//                // 이후 로직 작성
//            }
//        }
        if (account == null) {
            throw new IllegalArgumentException("kakao_account is missing in attributes");
        }

        return OAuth2UserInfo.builder()
//                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
//                .profile((String) profile.get("profile_image_url"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
//                .name(name)
                .email(email)
//                .profile(profile)
                .memberKey(KeyGenerator.generateKey()) // memberKey는 애플리케이션에서 사용되는 외부 식별자로 사용될 수 있다. ex) 고객 코드, 사용자 번호 등.
                .build();
    }
}

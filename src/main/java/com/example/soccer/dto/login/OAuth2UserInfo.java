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
import java.util.Random;

@Builder
public record OAuth2UserInfo(
//        String name,
        String email
//        String profile
) {
                                        // registrationId는 "kakao"
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        System.out.println("Registration ID: " + registrationId);
        System.out.println("Full Attributes: " + attributes);
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
        System.out.println("Kakao Attributes: " + attributes);
        // 제너릭 오류 발생
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        System.out.println("Kakao Account: " + account);
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
        // 안전한 이메일 추출
        String email = account != null && account.containsKey("email")
                ? (String) account.get("email")
                : null;

        if (email == null) {
            throw new IllegalArgumentException("Email is missing from Kakao account");
        }

        return OAuth2UserInfo.builder()
//                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
//                .profile((String) profile.get("profile_image_url"))
                .build();
    }

    public Member toEntity() {
        // 랜덤한 숫자 10자리 + 랜덤한 특수문자와 문자
        String userId = generateRandomUserId();

        // 랜덤한 형식의 닉네임
        String nickname = generateRandomNickname();

        return Member.builder()
                .userId(userId)
                .password(null)
                .name(null)
                .email(email)
                .nickname(nickname)
                .type("kakao")
                .memberKey(KeyGenerator.generateKey()) // memberKey는 애플리케이션에서 사용되는 외부 식별자로 사용될 수 있다. ex) 고객 코드, 사용자 번호 등.
//                .grade(Grade.USER)
                .build();
    }
    // 랜덤한 userId 생성 (10자리 숫자 + 특수문자 + 문자)
    private String generateRandomUserId() {
        Random random = new Random();

        // 랜덤 숫자 10자리
        String randomDigits = String.valueOf(1000000000L + random.nextLong(999999999L)); // 10자리 숫자 생성

        // 랜덤 특수문자 (길이 1)
        String randomSpecialChar = generateRandomSpecialCharacter(random);

        // 랜덤 소문자 (길이 1)
        String randomLowercase = generateRandomLowercase(random);

        // 랜덤 userId 결합 (10자리 숫자 + 소문자 + 특수문자)
        return randomDigits + randomSpecialChar + randomLowercase;
    }

    // 랜덤 소문자 생성 (길이 4)
    private String generateRandomLowercase(Random random) {
        StringBuilder sb = new StringBuilder();
        String letters = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 4; i++) {
            sb.append(letters.charAt(random.nextInt(letters.length())));
        }
        return sb.toString();
    }

    // 랜덤 특수문자 생성
    private String generateRandomSpecialCharacter(Random random) {
        String[] specialSymbols = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=", "{", "}", "[", "]", "|", "\\", ":", ";", "'", "\"", "<", ">", ",", ".", "?", "/"};
        return specialSymbols[random.nextInt(specialSymbols.length)];
    }

    // 랜덤한 형식의 닉네임 생성 (7e2z7tlw와 같은 형식)
    private String generateRandomNickname() {
        Random random = new Random();
        StringBuilder nickname = new StringBuilder();

        // 7자리 랜덤 소문자 + 1자리 랜덤 숫자 + 1자리 랜덤 소문자
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";

        // 소문자 4자리
        for (int i = 0; i < 4; i++) {
            nickname.append(letters.charAt(random.nextInt(letters.length())));
        }

        // 숫자 2자리
        for (int i = 0; i < 2; i++) {
            nickname.append(digits.charAt(random.nextInt(digits.length())));
        }

        // 소문자 2자리
        for (int i = 0; i < 2; i++) {
            nickname.append(letters.charAt(random.nextInt(letters.length())));
        }

        return nickname.toString();
    }
}

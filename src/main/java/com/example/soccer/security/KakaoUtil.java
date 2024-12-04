//package com.example.soccer.security;
//
//import ch.qos.logback.core.status.ErrorStatus;
//import com.example.soccer.dto.login.KakaoDTO;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//
//
//@Component
//@Slf4j
//public class KakaoUtil { // 인가 코드로 Token 요청하는 로직
//
//    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
//    private String client;
//    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
//    private String redirect;
//
//    public KakaoDTO.OAuthToken requestToken(String accessCode) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", client);
//        params.add("redirect_url", redirect);
//        params.add("code", accessCode);
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        KakaoDTO.OAuthToken oAuthToken = null;
//
//        try {
//            oAuthToken = objectMapper.readValue(response.getBody(), KakaoDTO.OAuthToken.class);
//            log.info("oAuthToken : " + oAuthToken.getAccess_token());
//        } catch (JsonProcessingException e) {
//            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
//        }
//        return oAuthToken;
//    }
//    public KakaoDTO.KakaoProfile requestProfile(KakaoDTO.OAuthToken oAuthToken) {
//        RestTemplate restTemplate2 = new RestTemplate();
//        HttpHeaders headers2 = new HttpHeaders();
//
//        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
//
//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);
//
//        ResponseEntity<String> response2 = restTemplate2.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.GET,
//                kakaoProfileRequest,
//                String.class);
//        try {
//            kakaoProfile = objectMapper.readValue(response2.getBody(), KakaoDTO.KakaoProfile.class);
//        } catch (JsonProcessingException e) {
//            log.info(Arrays.toString(e.getStackTrace()));
//            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
//        }
//
//        return kakaoProfile;
//    }
//    }
//}

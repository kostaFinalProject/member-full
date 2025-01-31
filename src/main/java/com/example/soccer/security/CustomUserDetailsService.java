package com.example.soccer.security;

import com.example.soccer.domain.Member;
import com.example.soccer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
//import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationFilter;
//import org.springframework.security.oauth2.client.registration.OAuth2UserRequest;

@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository; // 사용자 정보를 저장하는 리포지토리

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new CustomUserDetails(member);
    }

//    // 이메일로 사용자 조회
//    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
//        Member member = memberRepository.findByEmail(email) // 이메일로 사용자 조회
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//        return new CustomUserDetails(member);
//    }



//    // 회원가입 우회 처리하려 했으나 시큐리티로 해결
//    @Override
//    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
//        // 사용자가 존재하지 않으면 회원가입 처리
//        Member member = memberRepository.findByUserId(userId)
//                .orElseGet(() -> {
//                    // 회원가입 처리 (예: 사용자 정보를 생성하고 저장)
//                    Member newMember = new Member();
//                    newMember.setUserId(userId);
//                    newMember.setPassword("defaultPassword");  // 기본 비밀번호 (후에 암호화 필요)
//                    newMember.setName("New User");
//                    newMember.setNickname("NewNickname");
//                    newMember.setEmail("newemail@example.com");
//                    newMember.setPhone("010-0000-0000");
//                    newMember.setPostcode("00000");
//                    newMember.setRoadAddress("서울시");
//                    newMember.setDetailAddress("상세주소");
//
//                    // 회원가입된 사용자 정보 저장
//                    memberRepository.save(newMember);
//                    return newMember;
//                });
//
//        return new CustomUserDetails(member);
//    }
}

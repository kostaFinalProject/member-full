package com.example.soccer.security;

import com.example.soccer.domain.Member;
import com.example.soccer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository; // 사용자 정보를 저장하는 리포지토리

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
        return new CustomUserDetails(member);
    }

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

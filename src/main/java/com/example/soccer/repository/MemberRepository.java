package com.example.soccer.repository;

import com.example.soccer.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 동일 아이디 검증
    boolean existsByUserId(String userId); // 아이디 존재 시 true(0) 반환

    // 암호화 비밀번호 동일 검증 - userId로 회원을 찾는 메서드
    Optional<Member> findByUserId(String userId); // 객체 반환 null 값 허용

    // 현재 비밀번호 확인 > 복호화 후 리포지터리에서 조회를 제대로 못하는걸까?
//    boolean existsByPassword(String password);
//    Optional<Member> findByPassword(String password);

    // 동일 닉네임 검증
    boolean existsByNickname(String nickname);
    Optional<Member> findByNickname(String nickname);

    // 동일 이메일 검증
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    // 자신을 제외한 닉네임 검증                           > 이거 long type 맞나
    boolean existsByNicknameAndUserIdNot(String nickname, String userId);
    Optional<Member> findByNicknameAndUserIdNot(String nickname, String userId);
    
    // 멤버키
    Optional<Member> findByMemberKey(String memberKey);
}

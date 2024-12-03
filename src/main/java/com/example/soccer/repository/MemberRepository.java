package com.example.soccer.repository;

import com.example.soccer.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 동일 아이디 검증 및 암호화 비밀번호 동일 검증
    boolean existsByUserId(String userId);
    Optional<Member> findByUserId(String userId);

    // 현재 비밀번호 확인
//    boolean existsByPassword(String password);
//    Optional<Member> findByPassword(String password);

    // 동일 닉네임 검증
    boolean existsByNickname(String nickname);
    Optional<Member> findByNickname(String nickname);

    // 동일 이메일 검증
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    // 자신을 제외한 닉네임 검증                           > 이거 long type 맞나
    boolean existsByNicknameAndIdNot(String nickname, Long id);
    Optional<Member> findByNicknameAndIdNot(String nickname, Long id);
}
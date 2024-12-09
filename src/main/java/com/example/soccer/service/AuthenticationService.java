package com.example.soccer.service;

import static com.example.soccer.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.soccer.exception.ErrorCode.NO_ACCESS;

import com.example.soccer.domain.Member;
import com.example.soccer.exception.AuthException;
import com.example.soccer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final MemberRepository memberRepository;

    public Member getMemberOrThrow(String memberKey) {
        return memberRepository.findByMemberKey(memberKey)
                .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));
    }

    public void checkAccess(String memberKey, Member member) {
        if (!member.getMemberKey().equals(memberKey)) {
            throw new AuthException(NO_ACCESS);
        }
    }
}

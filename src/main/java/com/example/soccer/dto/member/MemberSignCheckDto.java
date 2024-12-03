package com.example.soccer.dto.member;

import com.example.soccer.domain.Address;
import com.example.soccer.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignCheckDto {
    private String userId;
    private String password;
    private String nickname;
    private String email;

    @Builder
    private MemberSignCheckDto(String userId,
                               String password,
                               String nickname,
                               String email){
        this.userId = userId;
        this.password=password;
        this.nickname=nickname;
        this.email=email;
    }
    public static MemberSignCheckDto checkMember(String userId,
                                                 String password,
                                                 String nickname,
                                                 String email) {
        return MemberSignCheckDto.builder().userId(userId)
                                           .password(password)
                                           .nickname(nickname)
                                           .email(email).build();
    }
}

package com.example.soccer.dto.member;

import com.example.soccer.domain.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpFormDto {
    private String userId;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    @Builder
    private MemberSignUpFormDto(String userId,
                                String password,
                                String name,
                                String nickname,
                                String email,
                                String phone,
                                String postcode,
                                String roadAddress,
                                String detailAddress){
        this.userId = userId;
        this.password=password;
        this.name=name;
        this.nickname=nickname;
        this.email=email;
        this.phone=phone;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}

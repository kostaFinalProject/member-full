package com.example.soccer.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 회원 수정용 DTO */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateFormDto {
//    private String userId;
    private String password;
    private String nickname;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    @Builder
    private MemberUpdateFormDto(String password,
                                String nickname,
                                String phone,
                                String postcode,
                                String roadAddress,
                                String detailAddress) {
//        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}

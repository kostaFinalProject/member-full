package com.example.soccer.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class MemberResponseDto {
    private String userId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    @Builder
    private MemberResponseDto(String userId,
                                String name,
                                String nickname,
                                String email,
                                String phone,
                                String postcode,
                                String roadAddress,
                                String detailAddress){
        this.userId = userId;
        this.name=name;
        this.nickname=nickname;
        this.email=email;
        this.phone=phone;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }

    public static MemberResponseDto readMemberResponseDto(String userId,
                                                         String name,
                                                         String nickname,
                                                         String email,
                                                         String phone,
                                                         String postcode,
                                                         String roadAddress,
                                                         String detailAddress) {
        return MemberResponseDto.builder().userId(userId)
                                        .name(name)
                                        .nickname(nickname)
                                        .email(email)
                                        .phone(phone)
                                        .postcode(postcode)
                                        .roadAddress(roadAddress)
                                        .detailAddress(detailAddress).build();
    }
}

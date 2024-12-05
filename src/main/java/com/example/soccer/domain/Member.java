package com.example.soccer.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="member")
@Getter            // 상속받은 클래스나 동일 패키지 내 클래스에서만 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 필수
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;


    // 선택
    @Column(name = "phone")
    private String phone;

    @Column(name = "profile")
    private String profile;

    @Embedded
    private Address address;

    // 자동 입력 값
    @Column(name = "point")
    private int point; // 기본값 0(스택) cf. integer : null(힙)

    @Enumerated(value = EnumType.STRING) // STRING type 지정
    @Column(name = "point_grade")
    private PointGrade pointGrade;

    @Enumerated(value = EnumType.STRING)
    private Grade grade;

    private int report_count;

    private int follower_count;

    private String type;

    @Column(nullable = false, unique = true) // memberKey는 이메일이나 다른 민감한 정보를 외부에 노출하지 않고, 사용자를 식별할 수 있는 비공개 키로 활용
    private String memberKey;


    @Builder
    private Member(String userId,
                   String password,
                   String name,
                   String nickname,
                   String email,
                   String phone,
                   String profile,
                   Address address,
                   int point,
                   PointGrade pointGrade,
                   int report_count,
                   int follower_count,
//                   String type, // > 왜 안넣어도 되는가
                   String memberKey) {
        this.userId = userId;
        this.password=password;
        this.name=name;
        this.nickname=nickname;
        this.email=email;
        this.phone=phone;
        this.profile=profile;
        this.address=address;
        this.point=point;
        this.pointGrade=pointGrade;
        this.grade=Grade.USER;
        this.report_count=report_count;
        this.follower_count=follower_count;
        this.type="app";
        this.memberKey=memberKey;
    }

    /**
     * Member 등급은 이메일 도메인으로 판단
     * ex) 사내 이메일 도메인: @company.com
     * @company.com이 이메일 주소면 ADMIN, 그렇지 않으면 CUSTOMER
     * ex) test@test.com -> CUSTOMER
     * ex) test@company.com -> ADMIN
     */ // service 검증 폼
    public static Member createMember(String userId,
                                      String password,
                                      String name,
                                      String nickname,
                                      String email,
                                      String phone,
                                      Address address) {
        return Member.builder().userId(userId)
                                .password(password)
                                .name(name)
                                .nickname(nickname)
                                .email(email)
                                .phone(phone)
                                .address(address).build();
    }
    public void updateMember(String password,
                             String nickname,
                             String phone,
                             Address address) {
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }
}

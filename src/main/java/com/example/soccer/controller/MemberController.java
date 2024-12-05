package com.example.soccer.controller;

import com.example.soccer.aop.TokenApi;
import com.example.soccer.dto.login.LoginDto;
import com.example.soccer.dto.login.LoginResponseDto;
import com.example.soccer.dto.login.RefreshTokenResponse;
import com.example.soccer.dto.member.MemberResponseDto;
import com.example.soccer.dto.member.MemberSignCheckDto;
import com.example.soccer.dto.member.MemberSignUpFormDto;
import com.example.soccer.dto.member.MemberUpdateFormDto;
import com.example.soccer.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    /** 회원가입 아이디, 닉네임, 이메일, 비밀번호 중복확인 */
    @PostMapping("/signcheck")
    public ResponseEntity<?> checkMember(@RequestBody MemberSignCheckDto signCheckDto) {

        boolean isAvailable = memberService.checkMember(signCheckDto);
        // 중복 여부에 따라 상태 코드와 메시지를 설정
        if (isAvailable) {
            return ResponseEntity.status(HttpStatus.OK).body(0);  // 사용 가능
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(1);  // 중복 존재
        }
    }
    /** 회원 가입 */
    @PostMapping("/signup")
    public ResponseEntity<?> saveMember(@RequestBody MemberSignUpFormDto signUpFormDto){
        memberService.saveMember(signUpFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입에 성공했습니다.");
        // 응답 포함 메시지 반환이지 HTTP 201 상태와 메시지 반환
    }

    /** 회원 조회 */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getMember(@PathVariable String userId) {
        // 요청에서 직접 전달된 memberId 사용
        MemberResponseDto responseDto = memberService.getMember(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    /** 회원 수정 */
    @PutMapping("/{userId}") // Long은 null값을 허용
    public ResponseEntity<?> updateMember(@PathVariable String userId,
                                          @RequestBody MemberUpdateFormDto updateFormDto) {
        memberService.updateMember(userId, updateFormDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 수정되었습니다.");
    }
    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String tokens = memberService.login(loginDto);
        String[] splitTokens = tokens.split(":");
        return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.createLoginResponseDto(
                "Bearer " + splitTokens[0], "Bearer " + splitTokens[1]));
    }
    /** Access Token 재발급 */
    @TokenApi
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String jwtRefreshToken = refreshToken.replace("Bearer ", "");
        String newAccessToken = memberService.refreshAccessToken(jwtRefreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(RefreshTokenResponse.createRefreshTokenResponse(
                "Bearer " + newAccessToken));
    }
}

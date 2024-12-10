package com.example.soccer.service;

import com.example.soccer.domain.Address;
import com.example.soccer.domain.Member;
import com.example.soccer.dto.login.LoginDto;
import com.example.soccer.dto.member.MemberResponseDto;
import com.example.soccer.dto.member.MemberSignCheckDto;
import com.example.soccer.dto.member.MemberSignUpFormDto;
import com.example.soccer.dto.member.MemberUpdateFormDto;
import com.example.soccer.repository.MemberRepository;
import com.example.soccer.security.JwtUtil;
import com.example.soccer.service.EntityValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EntityValidationService entityValidationService;
//    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    /** 회원 가입 */
    public void saveMember(MemberSignUpFormDto memberSingUpFormDto) {
        if (entityValidationService.existUserId(memberSingUpFormDto.getUserId())) {
            throw new IllegalArgumentException("이미 가입된 아이디가 존재합니다.");
        }

        Address address = Address.createAddress(
                memberSingUpFormDto.getPostcode(),
                memberSingUpFormDto.getRoadAddress(),
                memberSingUpFormDto.getDetailAddress()
        );

        Member member = Member.createMember(
                memberSingUpFormDto.getUserId(),
                bCryptPasswordEncoder.encode(memberSingUpFormDto.getPassword()),
                memberSingUpFormDto.getName(),
                memberSingUpFormDto.getNickname(),
                memberSingUpFormDto.getEmail(),
                memberSingUpFormDto.getPhone(),
                address
        );
        memberRepository.save(member);
    }
    /** 회원가입 시 중복 조회 */ // front 입력 값과 DB 값 비교
    public boolean checkMember(MemberSignCheckDto signCheckDto) {
        // 아이디 중복 체크
        if (entityValidationService.existUserId(signCheckDto.getUserId())) {
            return false;  // 아이디 중복(true)되면 false 1 반환
        }
//        // 현재 비밀번호 확인 (비밀번호는 입력된 비밀번호와 암호화된 비밀번호를 비교)
//        if (!entityValidationService.existPassword(signCheckDto.getUserId(), signCheckDto.getPassword())) {
//            return false;  // 비밀번호 일치하면 false 1 반환
//        }
        // 현재 닉네임 확인
        if (entityValidationService.existNickname(signCheckDto.getNickname())) {
            return false;  // 닉네임 중복되면 false 1 반환
        }
        // 이메일 중복 체크
        if (entityValidationService.existEmail(signCheckDto.getEmail())) {
            return false;  // 이메일 중복되면 false 1 반환
        }
        // 중복이 없으면 true 반환
        return true; // 0(사용가능) - false 1(중복존재)  * 비밀번호 번호 일치 false 1 반환
    }
    /** 회원 조회 */
    public MemberResponseDto getMember(String userId) {
        Member findMember = entityValidationService.validateMember(userId);

        return MemberResponseDto.readMemberResponseDto(
                findMember.getUserId(),
                findMember.getName(),
                findMember.getNickname(),
                findMember.getEmail(),
                findMember.getPhone(),
                findMember.getAddress().getPostcode(),
                findMember.getAddress().getRoadAddress(),
                findMember.getAddress().getDetailAddress()
        );
    }
    /** 회원 수정 */
    public void updateMember(String userId, MemberUpdateFormDto memberUpdateFormDto) {
        // memberId를 통해 기존 회원을 조회하고 유효성 검사
        Member findMember = entityValidationService.validateMember(userId);

        // 중복된 닉네임으로 수정 가능
        boolean isNicknameValidate = entityValidationService.existNicknameExceptMe(memberUpdateFormDto.getNickname(), userId);

        // 기존 값들과 비교
        boolean isSamePassword = bCryptPasswordEncoder.matches(memberUpdateFormDto.getPassword(), findMember.getPassword());
        boolean isSamePhone = findMember.getPhone().equals(memberUpdateFormDto.getPhone());
        boolean isSameNickname= findMember.getPhone().equals(memberUpdateFormDto.getNickname());

            // 기존 주소와 새로운 주소 비교
        Address currentAddress = findMember.getAddress();
        boolean isSameAddress = currentAddress.getPostcode().equals(memberUpdateFormDto.getPostcode()) &&
                currentAddress.getRoadAddress().equals(memberUpdateFormDto.getRoadAddress()) &&
                currentAddress.getDetailAddress().equals(memberUpdateFormDto.getDetailAddress());

        // 모든 필드가 동일한 경우 예외 처리
        if (isSameNickname && isSamePassword && isSamePhone && isSameAddress) {
            throw new IllegalArgumentException("수정된 정보가 없습니다.");
        }

        // 새로운 주소 객체 생성
        Address newAddress = Address.createAddress(
                memberUpdateFormDto.getPostcode(),
                memberUpdateFormDto.getRoadAddress(),
                memberUpdateFormDto.getDetailAddress()
        );

        // 회원 정보 업데이트 (비밀번호 암호화 제외)
        findMember.updateMember(
                bCryptPasswordEncoder.encode(memberUpdateFormDto.getPassword()),
                memberUpdateFormDto.getNickname(),
                memberUpdateFormDto.getPhone(),
                newAddress
        );
        memberRepository.save(findMember); // 저장하는 로직이 없었다. 보석씨의 활약으로 회원정보 수정 성공
    }
    /** 회원 탈퇴 */
    @Transactional
    public void deleteMember(String email) {
        Member findMember = entityValidationService.validateMemberByEmail(email);
        memberRepository.delete(findMember);
    }

    /** 로그인 */
    public String login(LoginDto loginDto) {
        Member member = entityValidationService.validateMemberByUserId(loginDto.getUserId());

        if (!bCryptPasswordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(member.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail(), accessToken);

        return accessToken + ":" + refreshToken;
    }
    /** Access Token 재발급 */
    public String refreshAccessToken(String refreshToken) {
        return jwtUtil.refreshAccessToken(refreshToken);
    }
}

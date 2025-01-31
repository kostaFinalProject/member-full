package com.example.soccer.service;

import com.example.soccer.domain.Member;
//import com.example.soccer.domain.shop.Cart;
//import com.example.soccer.domain.shop.Item;
import com.example.soccer.domain.shop.Cart;
import com.example.soccer.domain.shop.Item;
import com.example.soccer.repository.MemberRepository;
//import com.example.soccer.repository.cart.CartRepository;
import com.example.soccer.repository.cart.CartRepository;
import com.example.soccer.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntityValidationService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 동일 아이디 검증
    public boolean existUserId(String userId) {
        return memberRepository.existsByUserId(userId); // true
    }
    // 현재 비밀번호 확인
//    public boolean existPassword(String password) { // 암호화 이전 코드
//        return memberRepository.existsByPassword(password); // true
//    }
//    // 암호화된 비밀번호와 입력된 비밀번호 비교
//    public boolean existPassword(String userId, String password) {
////        // UserId로 회원 정보 찾기 - null을 직접 처리하지 않고 안전하게 값을 다룰 수 있게 도와 > 이걸로 하면 닉네임, 이메일 중복 조회가 망가짐
////        Member memberOptional = memberRepository.findByUserId(userId)
////                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
////
////
////        String storedPassword = memberOptional.getPassword();
////        return bCryptPasswordEncoder.matches(password, storedPassword);
//        // UserId로 회원 정보 찾기 - null을 직접 처리하지 않고 안전하게 값을 다룰 수 있게 도와
//        Optional<Member> memberOptional = memberRepository.findByUserId(userId);
//        // > 디버깅해야함
//
//        // 회원이 없으면 false 반환 - 디버깅해야함
//        if (!memberOptional.isPresent()) {
//            return false; // 회원이 존재하지 않으면 비밀번호도 틀린 것
//        }
//
//        // 저장된 암호화된 비밀번호
//        String storedPassword = memberOptional.get().getPassword();
//        System.out.println("Stored Password: " + storedPassword); // 디버깅: 저장된 암호화 비밀번호
//        System.out.println("Input Password: " + password);        // 디버깅: 사용자가 입력한 비밀번호
//
//        // 입력된 비밀번호와 암호화된 비밀번호를 비교
//        // 입력된 비밀번호와 암호화된 비밀번호 비교
//        if (bCryptPasswordEncoder.matches(password, storedPassword)) {
//            // 비밀번호가 일치하면 true 반환
//            return true;
//        } else {
//            // 비밀번호가 불일치하면 false 반환
//            return false;
//        }
//    } // 일치하면 true(0) // 불일치하면 false(1) 반환

    /** 동일 닉네임 검증 */
    public boolean existNickname(String nickname) { return memberRepository.existsByNickname(nickname); }

    /** 동일 이메일 검증 */
    public boolean existEmail(String email) {
        return memberRepository.existsByEmail(email); // true
    }

    /** Member 검증 */
    public Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    /** Member UserId로 검증 - 로그인, 회원 조회, 회원 수정 */
    public Member validateMemberByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    /** Member Email로 검증 - 회원 탈퇴 */
    public Member validateMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
    }

    // 동일 닉네임 검증 - 내 자신은 빼고
    public boolean existNicknameExceptMe(String nickname, String userId) {
        return memberRepository.existsByNicknameAndUserIdNot(nickname, userId); // true
    }

    // Item 검증
    public Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다."));
    }

    public Cart validateCart(Long cartItemId) {
        return cartRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니 항목을 찾을 수 없습니다."));
    }
}

package com.aurora.aurora_was.member.service;

import com.aurora.aurora_was.member.dto.req.LoginReq;
import com.aurora.aurora_was.member.dto.req.SignupReq;
import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 🔒 비밀번호 암호화 기계 장착!

    @Transactional
    public void signup(SignupReq signupReq) {
        if (memberRepository.existsByEmail(signupReq.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(signupReq.email())
                // 🚨 쌩비번 대신 암호화해서 창고에 넣기! (예: 1234 -> $2a$10$xQwE...)
                .password(passwordEncoder.encode(signupReq.password()))
                .name(signupReq.name())
                .phone(signupReq.phone())
                .role("user")
                .build();

        memberRepository.save(member);
    }

    public Member login(LoginReq loginReq) {
        Member member = memberRepository.findByEmail(loginReq.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 🔒 입력한 비번과 DB의 암호화된 비번이 찰떡같이 맞는지 확인!
        if (!passwordEncoder.matches(loginReq.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
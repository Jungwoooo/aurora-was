package com.aurora.aurora_was.member.controller;

import com.aurora.aurora_was.member.dto.req.LoginReq;
import com.aurora.aurora_was.member.dto.req.SignupReq;
import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.service.MemberService;
import com.aurora.aurora_was.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member") // 이 컨트롤러의 기본 주소
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil; // 🎟️ 팔찌 발급 기계 장착!

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupReq signupReq) {
        memberService.signup(signupReq);
        return ResponseEntity.ok("회원가입이 완료되었습니다!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginReq loginReq) {
        // 1. 주방장한테 비번 맞는지 검사받고 오기
        Member loginMember = memberService.login(loginReq);

        // 🎟️ 2. 로그인 성공! JWT 팔찌(토큰) 생성하기
        String token = jwtUtil.generateToken(loginMember.getId(), loginMember.getEmail(), loginMember.getRole());

        // 3. 프론트엔드에 줄 선물(데이터) 포장하기
        Map<String, Object> response = new HashMap<>();
        response.put("message", "로그인 성공!");
        response.put("accessToken", token); // 🎁 짠! 여기에 진짜 토큰을 담아줍니다!
        response.put("id", loginMember.getId());
        response.put("name", loginMember.getName());
        response.put("role", loginMember.getRole());

        return ResponseEntity.ok(response);
    }
}
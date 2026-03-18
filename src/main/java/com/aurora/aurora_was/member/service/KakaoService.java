package com.aurora.aurora_was.member.service;

import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.repository.MemberRepository;
import com.aurora.aurora_was.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    // 🚨 문제가 되던 final 변수들은 싹 지웠습니다!

    public Map<String, Object> kakaoLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 1. 카카오에게 암호(code)를 주고 카카오 전용 '토큰' 받아오기
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("grant_type", "authorization_code");
            // 💡 여기서 바로 @Value 변수(kakaoRestApiKey)를 꺼내 씁니다!
            tokenParams.add("client_id", kakaoRestApiKey);
            tokenParams.add("redirect_uri", kakaoRedirectUri);
            tokenParams.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
            ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token", tokenRequest, String.class);

            JsonNode tokenNode = objectMapper.readTree(tokenResponse.getBody());
            String accessToken = tokenNode.get("access_token").asText();

            // 2. 카카오 전용 토큰으로 사용자 '프로필(이메일, 이름)' 뺏어오기
            HttpHeaders profileHeaders = new HttpHeaders();
            profileHeaders.setBearerAuth(accessToken);
            profileHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);
            ResponseEntity<String> profileResponse = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, profileRequest, String.class);

            JsonNode profileNode = objectMapper.readTree(profileResponse.getBody());

            // 🛡️ 1. 닉네임 꺼내기 (이제 필수 동의 했으니 무조건 들어옵니다!)
            String nickname = "오로라회원"; // 혹시 모를 기본값
            if (profileNode.has("properties") && profileNode.get("properties").has("nickname")) {
                nickname = profileNode.get("properties").get("nickname").asText();
            }

            // 🛡️ 2. 이메일 대신 카카오 고유 ID로 우리만의 이메일 만들기! (권한 없음 완벽 해결)
            String kakaoId = profileNode.get("id").asText();
            String email = "kakao_" + kakaoId + "@aurora.com";

            // 3. 우리 DB에 있는지 확인하고, 없으면 자동 회원가입!
            Optional<Member> optionalMember = memberRepository.findByEmail(email);
            Member member;
            if (optionalMember.isEmpty()) {
                member = Member.builder()
                        .email(email)       // 💡 kakao_12345678@aurora.com 형태로 저장됨!
                        .password("KAKAO_LOGIN_USER")
                        .name(nickname)     // 💡 카카오톡 이름으로 저장됨!
                        .role("user")
                        .phone("010-0000-0000")
                        .build();
                member = memberRepository.save(member);
            } else {
                member = optionalMember.get();
            }

            // 4. 🚀 로그인 성공! 토큰 만들고 포장지에 담기!
            String token = jwtUtil.generateToken(member.getId(), member.getEmail(), member.getRole());

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", member.getId());
            result.put("name", member.getName());
            result.put("role", member.getRole());
            result.put("accessToken", token);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 로그인 중 서버 에러 발생!");
        }
    }
}
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
import org.springframework.stereotype.Component;
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
    private final JwtUtil jwtUtil; // 💡 팔찌 공장 주입!

    // 🚨 원장님의 카카오 대표 REST API 키 입력!
    private final String KAKAO_REST_API_KEY = kakaoRestApiKey;
    private final String REDIRECT_URI = kakaoRedirectUri;

    public Map<String, Object> kakaoLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 1. 카카오에게 암호(code)를 주고 카카오 전용 '토큰' 받아오기
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("grant_type", "authorization_code");
            tokenParams.add("client_id", KAKAO_REST_API_KEY);
            tokenParams.add("redirect_uri", REDIRECT_URI);
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

            // 이름과 이메일 추출!
            String nickname = profileNode.get("properties").get("nickname").asText();
            String email = profileNode.get("kakao_account").get("email").asText();

            // 3. 우리 DB에 있는지 확인하고, 없으면 자동 회원가입!
            Optional<Member> optionalMember = memberRepository.findByEmail(email);
            Member member;
            if (optionalMember.isEmpty()) {
                member = Member.builder()
                        .email(email)
                        .password("KAKAO_LOGIN_USER")
                        .name(nickname)
                        .role("USER")
                        .build();
                member = memberRepository.save(member); // 💡 저장 후 member 객체 갱신 (ID를 얻기 위해)
            } else {
                member = optionalMember.get();
            }

            // 4. 🚀 로그인 성공! 토큰 만들고 포장지에 담기!
            String token = jwtUtil.generateToken(member.getId(), member.getEmail(), member.getRole());

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", member.getId());
            result.put("name", member.getName());
            result.put("role", member.getRole());
            result.put("accessToken", token); // 프론트가 기다리는 이름!

            return result; // 🎁 포장된 상자 통째로 반환!
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 로그인 중 서버 에러 발생!");
        }
    }
}
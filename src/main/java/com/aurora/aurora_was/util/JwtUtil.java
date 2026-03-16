package com.aurora.aurora_was.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🚨 절대 털리면 안 되는 마스터 암호키! (실무에서는 application.properties에 숨깁니다)
    // 256비트 이상의 아주아주 긴 임의의 문자열이어야 합니다.
    private final String SECRET_KEY = "AuroraPoleDanceSecretKeyForJwtAuthenticationSuperSecure";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 팔찌 유효기간: 1시간 (1000ms * 60초 * 60분)
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 🚀 팔찌(토큰) 발급 메서드!
    public String generateToken(Long id, String email, String role) {
        return Jwts.builder()
                .setSubject(email) // 이 팔찌의 주인 (이메일)
                .claim("id", id) // 🚨 팔찌에 내 번호(id)도 도장 쾅!
                .claim("role", role) // 이 사람의 권한 (USER / ADMIN)
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 마스터 암호키로 도장 쾅!
                .compact(); // 팔찌 완성!
    }
}
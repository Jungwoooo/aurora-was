package com.aurora.aurora_was.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    private Key key; // 💡 final을 빼고 선언만 해둡니다.

    // 팔찌 유효기간: 1시간
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 🚀 [핵심] 스프링이 @Value 값을 다 채운 후에 실행되는 함수!
    @PostConstruct
    public void init() {
        // 이제 jwtSecretKey는 null이 아니에요!
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    // 🚀 팔찌(토큰) 발급 메서드!
    public String generateToken(Long id, String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
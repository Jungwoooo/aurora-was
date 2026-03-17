package com.aurora.aurora_was.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 💡 OPTIONS 허용을 위해 필요!
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 💡 스프링 시큐리티 전용 CORS 설정 장착!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🚨 핵심: 브라우저가 찔러보는 사전 요청(OPTIONS)은 무조건 통과!
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 회원가입, 로그인은 토큰 없이 통과!
                        .requestMatchers("/api/admin/**", "/api/member/**", "/api/voucher/**", "/api/lesson/**", "/api/reservation/**").permitAll()
                        // 나머지는 전부 팔찌(토큰) 검사!
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // 💡 시큐리티 문지기가 사용할 'CORS 허가증' 발급 기계
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://13.125.61.216"
        )); // 프론트엔드 주소 확실하게 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 메서드들
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 내 정보(토큰 등) 인증 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 주소(/**)에 이 허가증 적용!
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
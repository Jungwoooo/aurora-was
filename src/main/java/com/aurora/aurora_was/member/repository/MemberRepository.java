package com.aurora.aurora_was.member.repository;

import com.aurora.aurora_was.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 💡 마법의 코드: 이메일만 넘겨주면 DB에 이미 똑같은 이메일이 있는지 검사해 줍니다!
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
}
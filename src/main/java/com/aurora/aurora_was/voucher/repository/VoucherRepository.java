package com.aurora.aurora_was.voucher.repository;

import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    // 💡 마법의 코드: "회원 번호(memberId) 줄 테니까, 이 사람 수강권 지갑 좀 찾아와!"
    Optional<Voucher> findByMemberId(Long memberId);

    Optional<Object> findByMember(Member member);
}

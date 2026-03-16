package com.aurora.aurora_was.voucher.service;

import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.repository.MemberRepository;
import com.aurora.aurora_was.voucher.dto.req.GrantVoucherReq;
import com.aurora.aurora_was.voucher.entity.Voucher;
import com.aurora.aurora_was.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void grantVoucher(GrantVoucherReq grantVoucherReq) {
        // 1. 진짜 있는 회원인지 창고에서 확인!
        Member member = memberRepository.findById(grantVoucherReq.memberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 이 회원의 수강권 지갑을 꺼내오거나, 없으면 0회짜리 새 지갑을 만듦!
        Voucher voucher = voucherRepository.findByMemberId(member.getId())
                .orElseGet(() -> Voucher.builder()
                        .member(member)
                        .remainingCount(0)
                        .build());

        // 3. 지갑에 횟수 추가! (예: 0 + 10 = 10회)
        voucher.addCount(grantVoucherReq.count());

        // 4. 창고에 다시 저장!
        voucherRepository.save(voucher);
    }
}
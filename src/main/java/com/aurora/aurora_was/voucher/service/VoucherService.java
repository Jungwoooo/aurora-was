package com.aurora.aurora_was.voucher.service;

import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.repository.MemberRepository;
import com.aurora.aurora_was.admin.dto.req.GrantVoucherReq;
import com.aurora.aurora_was.voucher.dto.res.SearchMyVoucherRes;
import com.aurora.aurora_was.voucher.entity.Voucher;
import com.aurora.aurora_was.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void grantVoucher(GrantVoucherReq grantVoucherReq) {
        Member member = memberRepository.findById(grantVoucherReq.memberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Optional<Voucher> optionalVoucher = voucherRepository.findByMemberId(member.getId());

        if (optionalVoucher.isPresent()) {
            // 💡 1. 기존 회원이면 연장 (또는 기간 유지)
            Voucher voucher = optionalVoucher.get();

            // 기준일 찾기
            LocalDate baseDate = voucher.getExpiredAt() != null && voucher.getExpiredAt().isAfter(LocalDate.now())
                    ? voucher.getExpiredAt()
                    : LocalDate.now();

            LocalDate newDate;
            if (grantVoucherReq.periodMonths() != null) {
                newDate = baseDate.plusMonths(grantVoucherReq.periodMonths());
            } else if (grantVoucherReq.customExpiredAt() != null) {
                newDate = grantVoucherReq.customExpiredAt(); // 💡 직접 지정 (기간을 줄이는 것도 가능!)
            } else {
                newDate = voucher.getExpiredAt(); // 🚨 핵심: 둘 다 null이면 기존 만료일 유지!
            }

            voucher.addCount(grantVoucherReq.count(), newDate);

        } else {
            // 💡 2. 완전 신규 회원일 경우
            // 신규 회원인데 기간을 아예 안 주면 무한 수강권이 되므로 에러 처리!
            if (grantVoucherReq.periodMonths() == null && grantVoucherReq.customExpiredAt() == null) {
                throw new IllegalArgumentException("신규 회원에게 수강권을 발급할 때는 유효기간을 반드시 설정해주세요.");
            }

            LocalDate newDate = grantVoucherReq.periodMonths() != null
                    ? LocalDate.now().plusMonths(grantVoucherReq.periodMonths())
                    : grantVoucherReq.customExpiredAt();

            Voucher newVoucher = Voucher.builder()
                    .member(member)
                    .remainingCount(grantVoucherReq.count())
                    .expiredAt(newDate)
                    .build();
            voucherRepository.save(newVoucher);
        }
    }

    // 🚀 수강생 본인 수강권 조회
    @Transactional(readOnly = true)
    public SearchMyVoucherRes getMyVoucher(Long memberId) {
        return voucherRepository.findByMemberId(memberId)
                .map(voucher -> new SearchMyVoucherRes(voucher.getRemainingCount(), voucher.getExpiredAt()))
                .orElse(new SearchMyVoucherRes(0, null)); // 수강권이 없으면 0회 반환!
    }
}
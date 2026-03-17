package com.aurora.aurora_was.admin.dto.res;

import java.time.LocalDate;

public record SearchMemberListRes (
        Long memberId,
        String email, // 💡 원장님 말씀대로 이메일이 아이디!
        String name,
        int remainingCount, // 남은 횟수
        LocalDate expiredAt // 만료일
) {
}

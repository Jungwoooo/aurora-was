package com.aurora.aurora_was.admin.dto.req;

import java.time.LocalDate;

public record GrantVoucherReq (
        Long memberId,
        int count,
        Integer periodMonths, // 💡 1, 2, 3, 6개월 (라디오 버튼용)
        LocalDate customExpiredAt // 💡 직접 지정 날짜용
) {
}

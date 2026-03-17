package com.aurora.aurora_was.voucher.dto.res;

import java.time.LocalDate;

public record SearchMyVoucherRes (
        int remainingCount,
        LocalDate expiredAt
) {
}

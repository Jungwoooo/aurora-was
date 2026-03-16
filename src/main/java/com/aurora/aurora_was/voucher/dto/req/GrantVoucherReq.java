package com.aurora.aurora_was.voucher.dto.req;

public record GrantVoucherReq (
        Long memberId,
        int count
) {
}

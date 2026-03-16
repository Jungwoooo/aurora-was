package com.aurora.aurora_was.voucher.controller;

import com.aurora.aurora_was.voucher.dto.req.GrantVoucherReq;
import com.aurora.aurora_was.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // 원장님이 수강권 횟수를 쏴주는 API
    @PostMapping("/grant")
    public ResponseEntity<String> grantVoucher(@RequestBody GrantVoucherReq grantVoucherReq) {
        voucherService.grantVoucher(grantVoucherReq);
        return ResponseEntity.ok(grantVoucherReq.count() + "회 수강권이 성공적으로 지급되었습니다!");
    }
}
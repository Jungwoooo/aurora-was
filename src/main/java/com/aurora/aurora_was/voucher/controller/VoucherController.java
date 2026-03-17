package com.aurora.aurora_was.voucher.controller;

import com.aurora.aurora_was.voucher.dto.res.SearchMyVoucherRes;
import com.aurora.aurora_was.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // 내 수강권 조회 API
    @GetMapping("/my")
    public ResponseEntity<SearchMyVoucherRes> getMyVoucher(@RequestParam Long memberId) {
        return ResponseEntity.ok(voucherService.getMyVoucher(memberId));
    }

}
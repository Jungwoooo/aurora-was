package com.aurora.aurora_was.admin.controller;

import com.aurora.aurora_was.admin.dto.req.CreateLessonReq;
import com.aurora.aurora_was.lesson.service.LessonService;
import com.aurora.aurora_was.admin.dto.res.SearchReservationListRes;
import com.aurora.aurora_was.admin.dto.res.SearchTodayReservationRes;
import com.aurora.aurora_was.admin.dto.res.SearchMemberListRes;
import com.aurora.aurora_was.member.service.MemberService;
import com.aurora.aurora_was.reservation.service.ReservationService;
import com.aurora.aurora_was.admin.dto.req.GrantVoucherReq;
import com.aurora.aurora_was.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VoucherService voucherService;
    private final LessonService lessonService;
    private final ReservationService reservationService;
    private final MemberService memberService;

    @GetMapping("/member/list")
    public ResponseEntity<List<SearchMemberListRes>> getMembers() {
        return ResponseEntity.ok(memberService.getAllMembersWithVoucher());
    }

    // 원장님이 수강권 횟수를 쏴주는 API
    @PostMapping("/voucher/grant")
    public ResponseEntity<String> grantVoucher(@RequestBody GrantVoucherReq grantVoucherReq) {
        voucherService.grantVoucher(grantVoucherReq);
        // 💡 "0회" 라는 말을 빼고 자연스럽게 변경!
        return ResponseEntity.ok("수강권 정보가 성공적으로 업데이트되었습니다!");
    }

    @PostMapping("/lesson/create")
    public ResponseEntity<String> createLesson(@RequestBody CreateLessonReq createLessonReq) {
        lessonService.createLesson(createLessonReq);
        return ResponseEntity.ok("수업이 성공적으로 개설되었습니다!");
    }

    @GetMapping("/reservation/{memberId}")
    public ResponseEntity<List<SearchReservationListRes>> getMemberReservations(@PathVariable Long memberId) {
        return ResponseEntity.ok(reservationService.getMemberReservations(memberId));
    }

    // 관리자 강제 예약 취소 API
    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<String> forceCancelReservation(@PathVariable Long reservationId) {
        reservationService.forceCancelReservation(reservationId);
        return ResponseEntity.ok("강제 취소 및 횟수 복구 완료");
    }

    @GetMapping("/today")
    public ResponseEntity<List<SearchTodayReservationRes>> getDailySchedule(@RequestParam String date) {
        return ResponseEntity.ok(reservationService.getDailySchedule(date));
    }
}

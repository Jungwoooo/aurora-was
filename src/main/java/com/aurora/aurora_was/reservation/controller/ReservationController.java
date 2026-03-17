package com.aurora.aurora_was.reservation.controller;

import com.aurora.aurora_was.reservation.dto.req.CreateReservationReq;
import com.aurora.aurora_was.admin.dto.res.SearchReservationListRes;
import com.aurora.aurora_was.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation") // 💡 깔끔한 단수형!
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<String> createReservation(@RequestBody CreateReservationReq createReservationReq) {
        reservationService.createReservation(createReservationReq);
        return ResponseEntity.ok("예약이 완벽하게 확정되었습니다!");
    }

    // 내 예약 목록 조회 API
    @GetMapping("/my")
    public ResponseEntity<List<SearchReservationListRes>> getMyReservations(@RequestParam Long memberId) {
        return ResponseEntity.ok(reservationService.getMyReservations(memberId));
    }

    // 내 예약 취소 API
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelMyReservation(
            @PathVariable Long reservationId,
            @RequestParam Long memberId) {
        reservationService.cancelMyReservation(reservationId, memberId);
        return ResponseEntity.ok("예약이 정상적으로 취소되었습니다.");
    }

    /**
     * ADMIN
     */


}
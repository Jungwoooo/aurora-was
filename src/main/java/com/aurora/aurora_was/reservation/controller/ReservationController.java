package com.aurora.aurora_was.reservation.controller;

import com.aurora.aurora_was.reservation.dto.req.CreateReservationReq;
import com.aurora.aurora_was.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
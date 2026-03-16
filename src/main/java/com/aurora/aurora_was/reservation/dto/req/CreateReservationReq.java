package com.aurora.aurora_was.reservation.dto.req;

public record CreateReservationReq(
        Long memberId,
        Long lessonId
) {}
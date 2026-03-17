package com.aurora.aurora_was.admin.dto.res;

import java.time.LocalDateTime;

public record SearchReservationListRes (
        Long reservationId,   // 프론트의 res.reservationId 와 매칭!
        Long lessonId,        // 수업 고유 번호
        String lessonTitle,   // 프론트의 res.lessonTitle 과 매칭!
        String lessonInstructor,
        LocalDateTime startDateTime, // 수업 시작 시간 (프론트의 res.startDateTime)
        LocalDateTime endDateTime
) {
}

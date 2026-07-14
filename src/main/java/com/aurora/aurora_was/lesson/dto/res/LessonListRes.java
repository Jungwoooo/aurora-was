package com.aurora.aurora_was.lesson.dto.res;

import java.time.LocalDateTime;

// 🚀 깔끔한 record로 선언!
public record LessonListRes(
        Long id,
        String title,
        String instructor,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int capacity,
        Long reserved // 현재 예약된 인원 수
) {
}
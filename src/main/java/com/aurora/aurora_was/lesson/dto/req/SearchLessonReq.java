package com.aurora.aurora_was.lesson.dto.req;

public record SearchLessonReq(
        Long id,
        String title,
        String instructor,
        String time,     // 화면에 보여줄 시간 (예: "19:00")
        int capacity,    // 정원
        int reserved       // 현재 예약된 인원 (핵심!)
) {}
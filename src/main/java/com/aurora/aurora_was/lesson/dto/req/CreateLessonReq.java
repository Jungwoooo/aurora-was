package com.aurora.aurora_was.lesson.dto.req;

public record CreateLessonReq (
        String title,
        String instructor,
        String startTime,
        int capacity
) {
}

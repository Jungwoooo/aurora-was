package com.aurora.aurora_was.admin.dto.req;

public record CreateLessonReq (
        String title,
        String instructor,
        String startTime,
        String endTime, // 💡 추가!
        int capacity
) {
}

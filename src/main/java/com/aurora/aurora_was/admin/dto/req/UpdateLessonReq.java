package com.aurora.aurora_was.admin.dto.req;

import java.time.LocalDateTime;

public record UpdateLessonReq (
        String title,
        String instructor,
        String startTime,
        String endTime,
        int capacity
) {
}

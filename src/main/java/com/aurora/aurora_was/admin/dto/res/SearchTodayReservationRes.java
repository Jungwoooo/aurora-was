package com.aurora.aurora_was.admin.dto.res;

import java.util.List;

public record SearchTodayReservationRes (
        Long lessonId,
        String title,
        String instructor,
        String startTime,
        String endTime,
        int capacity,
        int reservedCount,
        List<String> reservedMembers // 💡 이 수업을 예약한 회원들의 이메일(또는 이름) 목록!
) {
}

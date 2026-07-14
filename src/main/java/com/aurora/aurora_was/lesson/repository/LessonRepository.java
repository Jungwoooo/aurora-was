package com.aurora.aurora_was.lesson.repository;

import com.aurora.aurora_was.lesson.dto.res.LessonListRes;
import com.aurora.aurora_was.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // 💡 마법의 코드: "시작 시간과 끝 시간(하루치)을 줄 테니, 그 사이의 수업을 다 가져와!"
    List<Lesson> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 🚀 [마법의 쿼리] 수업 100개가 있어도 DB 쿼리는 딱 1번만 나갑니다! (N+1 문제 완벽 해결)
    @Query("""
        SELECT new com.aurora.aurora_was.lesson.dto.res.LessonListRes(
            l.id, l.title, l.instructor, l.startTime, l.endTime, l.capacity, COUNT(r.id)
        )
        FROM Lesson l
        LEFT JOIN Reservation r ON r.lesson.id = l.id
        WHERE l.startTime BETWEEN :start AND :end
        GROUP BY l.id
        ORDER BY l.startTime ASC
    """)
    List<LessonListRes> findLessonsWithReservationCount(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
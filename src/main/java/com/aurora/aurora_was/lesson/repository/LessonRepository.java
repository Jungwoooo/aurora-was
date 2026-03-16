package com.aurora.aurora_was.lesson.repository;

import com.aurora.aurora_was.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // 💡 마법의 코드: "시작 시간과 끝 시간(하루치)을 줄 테니, 그 사이의 수업을 다 가져와!"
    List<Lesson> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
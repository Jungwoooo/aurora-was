package com.aurora.aurora_was.lesson.service;

import com.aurora.aurora_was.lesson.dto.req.CreateLessonReq;
import com.aurora.aurora_was.lesson.dto.req.SearchLessonReq;
import com.aurora.aurora_was.lesson.entity.Lesson;
import com.aurora.aurora_was.lesson.repository.LessonRepository;
import com.aurora.aurora_was.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void createLesson(CreateLessonReq createLessonReq) {
        // 프론트가 보낸 글자(String)를 진짜 시간(LocalDateTime) 객체로 변환!
        LocalDateTime parsedTime = LocalDateTime.parse(createLessonReq.startTime());

        Lesson lesson = Lesson.builder()
                .title(createLessonReq.title())
                .instructor(createLessonReq.instructor())
                .startTime(parsedTime)
                .capacity(createLessonReq.capacity())
                .build();

        lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public List<SearchLessonReq> getLessonsByDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr); // "2026-03-25" 파싱
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // 23:59:59

        List<Lesson> lessons = lessonRepository.findByStartTimeBetween(startOfDay, endOfDay);

        return lessons.stream().map(lesson -> {
            int booked = reservationRepository.countByLesson(lesson); // 이 수업 예약자 수 세기
            String time = lesson.getStartTime().toLocalTime().toString(); // "19:00"

            return new SearchLessonReq(
                    lesson.getId(),
                    lesson.getTitle(),
                    lesson.getInstructor(),
                    time,
                    lesson.getCapacity(),
                    booked
            );
        }).collect(Collectors.toList());
    }
}
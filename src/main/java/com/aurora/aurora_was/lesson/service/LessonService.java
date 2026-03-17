package com.aurora.aurora_was.lesson.service;

import com.aurora.aurora_was.admin.dto.req.CreateLessonReq;
import com.aurora.aurora_was.lesson.dto.res.SearchLessonRes;
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
        // 💡 프론트가 보낸 글자(String)를 진짜 시간 객체로 변환! (endTime 추가)
        LocalDateTime parsedStartTime = LocalDateTime.parse(createLessonReq.startTime());
        LocalDateTime parsedEndTime = LocalDateTime.parse(createLessonReq.endTime());

        Lesson lesson = Lesson.builder()
                .title(createLessonReq.title())
                .instructor(createLessonReq.instructor())
                .startTime(parsedStartTime)
                .endTime(parsedEndTime) // 💡 엔티티에 저장!
                .capacity(createLessonReq.capacity())
                .build();

        lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public List<SearchLessonRes> getLessonsByDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Lesson> lessons = lessonRepository.findByStartTimeBetween(startOfDay, endOfDay);

        return lessons.stream().map(lesson -> {
            int booked = reservationRepository.countByLesson(lesson);

            // 💡 시작 시간과 종료 시간을 각각 "19:00", "19:50" 모양의 글자로 뽑아냅니다!
            String startTimeStr = lesson.getStartTime().toLocalTime().toString();
            String endTimeStr = lesson.getEndTime().toLocalTime().toString();

            return new SearchLessonRes(
                    lesson.getId(),
                    lesson.getTitle(),
                    lesson.getInstructor(),
                    startTimeStr, // 💡 바뀐 이름 적용
                    endTimeStr,   // 💡 종료 시간 추가
                    lesson.getCapacity(),
                    booked
            );
        }).collect(Collectors.toList());
    }
}
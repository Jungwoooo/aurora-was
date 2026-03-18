package com.aurora.aurora_was.lesson.service;

import com.aurora.aurora_was.admin.dto.req.CreateLessonReq;
import com.aurora.aurora_was.admin.dto.req.UpdateLessonReq;
import com.aurora.aurora_was.lesson.dto.res.SearchLessonRes;
import com.aurora.aurora_was.lesson.entity.Lesson;
import com.aurora.aurora_was.lesson.repository.LessonRepository;
import com.aurora.aurora_was.reservation.entity.Reservation;
import com.aurora.aurora_was.reservation.repository.ReservationRepository;
import com.aurora.aurora_was.voucher.entity.Voucher;
import com.aurora.aurora_was.voucher.repository.VoucherRepository;
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
    private final VoucherRepository voucherRepository;

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

    @Transactional
    public void updateLesson(Long id, UpdateLessonReq updateLessonReq) {
        // 1. 기존 수업 찾기
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 수업을 찾을 수 없습니다."));

        LocalDateTime parsedStartTime = LocalDateTime.parse(updateLessonReq.startTime());
        LocalDateTime parsedEndTime = LocalDateTime.parse(updateLessonReq.endTime());

        // 2. 내용 바꾸기 (이러면 끝! JPA가 알아서 DB에 UPDATE 쿼리를 날려줍니다)
        lesson.updateLesson(
                updateLessonReq.title(),
                updateLessonReq.instructor(),
                parsedStartTime,
                parsedEndTime,
                updateLessonReq.capacity()
        );
    }

    // 🗑️ 수업 삭제 (Soft Delete)
    @Transactional
    public void deleteLesson(Long lessonId) {
        // 1. 수업 조회
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수업입니다."));

        // 2. 이 수업에 걸려있는 예약 목록 조회
        // 💡 엔티티에 @SQLRestriction("use_yn = 'Y'")가 있어서 취소된 건 알아서 걸러집니다!
        List<Reservation> reservations = reservationRepository.findByLesson(lesson);

        // 3. 예약 건수만큼 반복하면서 수강권 복구 및 예약 취소
        for (Reservation reservation : reservations) {

            // 3-1. 예약한 회원의 수강권 조회
            Voucher voucher = (Voucher) voucherRepository.findByMember(reservation.getMember())
                    .orElseThrow(() -> new IllegalArgumentException("회원의 수강권 정보를 찾을 수 없습니다."));

            // 3-2. 수강권 횟수 복구 (+1) / 만료일은 기존 만료일 그대로 유지
            voucher.addCount(1, voucher.getExpiredAt());

            // 3-3. 예약 취소
            // 💡 @SQLDelete 덕분에 알아서 UPDATE reservation SET use_yn = 'N' 이 날아갑니다.
            reservationRepository.delete(reservation);
        }

        // 4. 수업 삭제
        // 💡 마찬가지로 UPDATE lesson SET use_yn = 'N' 이 날아갑니다.
        lessonRepository.delete(lesson);
    }
}
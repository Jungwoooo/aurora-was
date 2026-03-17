package com.aurora.aurora_was.reservation.service;

import com.aurora.aurora_was.lesson.entity.Lesson;
import com.aurora.aurora_was.lesson.repository.LessonRepository;
import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.repository.MemberRepository;
import com.aurora.aurora_was.reservation.dto.req.CreateReservationReq;
import com.aurora.aurora_was.admin.dto.res.SearchReservationListRes;
import com.aurora.aurora_was.admin.dto.res.SearchTodayReservationRes;
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

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final LessonRepository lessonRepository;
    private final VoucherRepository voucherRepository;

    // 💡 @Transactional: "예약하다가 에러 나면 깎았던 횟수 다시 원래대로 돌려놔!" (안전장치)
    @Transactional
    public void createReservation(CreateReservationReq createReservationReq) {
        // 1. 창고에서 3가지(회원, 수업, 수강권 지갑)를 다 꺼내옵니다.
        Member member = memberRepository.findById(createReservationReq.memberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Lesson lesson = lessonRepository.findById(createReservationReq.lessonId())
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다."));

        if (reservationRepository.existsByMemberAndLesson(member, lesson)) {
            // 이미 예약했다면 뒤도 안 돌아보고 바로 에러 뿜뿜! (횟수 차감 안 됨)
            throw new IllegalArgumentException("이미 예약하신 수업입니다!");
        }

        Voucher voucher = voucherRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("보유하신 수강권이 없습니다. 원장님께 문의해주세요!"));

        // 🎟️ 2. 수강권 횟수 1회 차감! (Voucher 엔티티에 만들어둔 메서드 호출)
        // (안에서 횟수 0 이하인지 검사해서 부족하면 에러를 뿜어냅니다!)
        voucher.deductCount();

        // ✍️ 3. 예약 장부에 기록 생성
        Reservation reservation = Reservation.builder()
                .member(member)
                .lesson(lesson)
                .build();

        // 4. 장부 저장!
        reservationRepository.save(reservation);
    }

    // 1. 내 예약 목록 조회 (수강생용)
    @Transactional(readOnly = true)
    public List<SearchReservationListRes> getMyReservations(Long memberId) {
        List<Reservation> reservations = reservationRepository.findByMemberId(memberId);

        return reservations.stream().map(res -> new SearchReservationListRes(
                res.getId(),
                res.getLesson().getId(),
                res.getLesson().getTitle(),
                res.getLesson().getInstructor(),
                res.getLesson().getStartTime(),
                res.getLesson().getEndTime()
        )).toList();
    }

    // 2. 수강생 본인 예약 취소 (전날 18시 룰 적용!)
    @Transactional
    public void cancelMyReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 내역이 없습니다."));

        // 본인 예약이 맞는지 검증
        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }

        // 🚨 핵심 로직: 전날 저녁 6시가 지났는지 검사!
        LocalDateTime lessonStartTime = reservation.getLesson().getStartTime();
        LocalDateTime deadline = lessonStartTime.minusDays(1).withHour(18).withMinute(0).withSecond(0);

        if (LocalDateTime.now().isAfter(deadline)) {
            // 시간이 지났으면 가차 없이 에러를 던집니다!
            throw new IllegalArgumentException("취소 가능 시간이 지났습니다. (수업 전날 18시 마감) 😭");
        }

        // 통과했다면? 수강권 횟수 1회 복구
        Voucher voucher = voucherRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("수강권 정보가 없습니다."));
        voucher.addCount(1, voucher.getExpiredAt());

        // 예약 삭제
        reservationRepository.delete(reservation);
    }

    /**
     * ADMIN
     */

    // 1. 특정 회원의 예약 내역 다 가져오기
    @Transactional(readOnly = true)
    public List<SearchReservationListRes> getMemberReservations(Long memberId) {
        // (💡 ReservationRepository에 findByMemberId 메서드가 있어야 합니다!)
        List<Reservation> reservations = reservationRepository.findByMemberId(memberId);

        return reservations.stream().map(res -> new SearchReservationListRes(
                res.getId(),
                res.getLesson().getId(),
                res.getLesson().getTitle(),
                res.getLesson().getInstructor(),
                res.getLesson().getStartTime(),
                res.getLesson().getEndTime()
        )).toList();
    }

    // 2. 관리자 권한으로 강제 취소 (전날 6시 룰 무시!)
    @Transactional
    public void forceCancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 내역이 없습니다."));

        // 1. 수강권 횟수 1회 복구
        Voucher voucher = voucherRepository.findByMemberId(reservation.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("수강권 정보가 없습니다."));

        // (기간은 그대로 두고 횟수만 +1)
        voucher.addCount(1, voucher.getExpiredAt());

        // 2. 예약 내역 삭제
        reservationRepository.delete(reservation);
    }

    // 🚀 관리자 메인: 특정 날짜의 수업 & 예약자 명단 조회
    @Transactional(readOnly = true)
    public List<SearchTodayReservationRes> getDailySchedule(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 1. 그날의 모든 수업 찾기
        List<Lesson> lessons = lessonRepository.findByStartTimeBetween(startOfDay, endOfDay);

        // 2. 수업마다 예약자 명단 끼워 넣기
        return lessons.stream().map(lesson -> {
            // 이 수업에 예약된 내역들 가져오기
            List<Reservation> reservations = reservationRepository.findByLesson(lesson);

            // 예약한 사람들의 이메일만 쏙 빼서 리스트로 만들기
            List<String> members = reservations.stream()
                    .map(res -> res.getMember().getName())
                    .toList();

            return new SearchTodayReservationRes(
                    lesson.getId(),
                    lesson.getTitle(),
                    lesson.getInstructor(),
                    lesson.getStartTime().toLocalTime().toString(), // "19:00"
                    lesson.getEndTime().toLocalTime().toString(),   // "19:50"
                    lesson.getCapacity(),
                    members.size(),
                    members // 💡 명단 탑재 완료!
            );
        }).toList();
    }

}
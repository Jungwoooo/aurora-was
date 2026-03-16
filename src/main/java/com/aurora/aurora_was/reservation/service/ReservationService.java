package com.aurora.aurora_was.reservation.service;

import com.aurora.aurora_was.lesson.entity.Lesson;
import com.aurora.aurora_was.lesson.repository.LessonRepository;
import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.member.repository.MemberRepository;
import com.aurora.aurora_was.reservation.dto.req.CreateReservationReq;
import com.aurora.aurora_was.reservation.entity.Reservation;
import com.aurora.aurora_was.reservation.repository.ReservationRepository;
import com.aurora.aurora_was.voucher.entity.Voucher;
import com.aurora.aurora_was.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
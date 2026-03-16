package com.aurora.aurora_was.reservation.repository;

import com.aurora.aurora_was.lesson.entity.Lesson;
import com.aurora.aurora_was.member.entity.Member;
import com.aurora.aurora_was.reservation.entity.Reservation;
import com.aurora.aurora_was.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<com.aurora.aurora_was.reservation.entity.Reservation, Long> {
    int countByLesson(Lesson lesson);
    boolean existsByMemberAndLesson(Member member, Lesson lesson);
}
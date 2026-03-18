package com.aurora.aurora_was.reservation.entity;

import com.aurora.aurora_was.lesson.entity.Lesson;
import com.aurora.aurora_was.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE reservation SET use_yn = 'N' WHERE id = ?")
@SQLRestriction("use_yn = 'Y'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 누가 예약했나?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 💡 무슨 수업을 예약했나?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @CreationTimestamp // 💡 "DB에 저장될 때 하이버네이트야 네가 알아서 현재 시간 딱 찍어라!"
    @Column(nullable = false)
    private LocalDateTime reservedAt; // 언제 예약 버튼을 눌렀나?

    @Column(nullable = false, name = "use_yn", length = 1)
    private String useYn;

    @PrePersist
    public void prePersist() {
        if (this.useYn == null) {
            this.useYn = "Y";
        }
    }

//    @Builder
//    public Reservation(Member member, Lesson lesson) {
//        this.member = member;
//        this.lesson = lesson;
//        this.reservedAt = LocalDateTime.now(); // 예약한 현재 시간 자동 기록
//    }
}
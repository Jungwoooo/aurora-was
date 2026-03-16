package com.aurora.aurora_was.lesson.entity;

import com.aurora.aurora_was.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 수업명 (예: 입문반)

    @Column(nullable = false)
    private String instructor; // 강사명 (예: 오로라)

    @Column(nullable = false)
    private LocalDateTime startTime; // 수업 시작 시간 (날짜 + 시간)

    @Column(nullable = false)
    private int capacity; // 정원 (예: 8명)

//    @Builder
//    public Lesson(String title, String instructor, LocalDateTime startTime, int capacity) {
//        this.title = title;
//        this.instructor = instructor;
//        this.startTime = startTime;
//        this.capacity = capacity;
//    }
}
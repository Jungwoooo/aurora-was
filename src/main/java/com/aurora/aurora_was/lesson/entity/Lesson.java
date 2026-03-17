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
    private String title;

    @Column(nullable = false)
    private String instructor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    // 🚨 수업 종료 시간 추가!
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int capacity;
}
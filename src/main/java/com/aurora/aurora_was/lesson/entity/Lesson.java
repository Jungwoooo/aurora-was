package com.aurora.aurora_was.lesson.entity;

import com.aurora.aurora_was.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE lesson SET use_yn = 'N' WHERE id = ?")
@SQLRestriction("use_yn = 'Y'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Column(nullable = false, name = "use_yn", length = 1)
    private String useYn;

    @PrePersist
    public void prePersist() {
        if (this.useYn == null) {
            this.useYn = "Y";
        }
    }

    public void updateLesson(String title, String instructor, LocalDateTime startTime, LocalDateTime endTime, int capacity) {
        this.title = title;
        this.instructor = instructor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
    }
}
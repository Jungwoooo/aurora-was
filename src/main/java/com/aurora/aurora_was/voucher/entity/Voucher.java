package com.aurora.aurora_was.voucher.entity;

import com.aurora.aurora_was.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE voucher SET use_yn = 'N' WHERE id = ?")
@SQLRestriction("use_yn = 'Y'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Voucher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int remainingCount;

    // 🚨 1. 만료일 컬럼 추가!
    @Column(nullable = false)
    private LocalDate expiredAt;

    @Column(nullable = false, name = "use_yn", length = 1)
    private String useYn;

    @PrePersist
    public void prePersist() {
        if (this.useYn == null) {
            this.useYn = "Y";
        }
    }

    @Builder
    public Voucher(Member member, int remainingCount, LocalDate expiredAt) {
        this.member = member;
        this.remainingCount = remainingCount;
        this.expiredAt = expiredAt;
    }

    // 🚨 2. 횟수 추가 + 만료일 덮어쓰기 로직
    public void addCount(int count, LocalDate newExpiredAt) {
        this.remainingCount += count;
        this.expiredAt = newExpiredAt; // 원장님 기획대로 새로운 날짜로 덮어씌웁니다!
    }

    // 🚨 3. 예약할 때 횟수 깎는 로직 (유효기간 검사 추가!)
    public void deductCount() {
        if (this.remainingCount <= 0) {
            throw new IllegalArgumentException("수강권 횟수가 부족합니다.");
        }
        if (this.expiredAt.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("수강권 유효기간이 만료되었습니다. 😭");
        }
        this.remainingCount--;
    }
}
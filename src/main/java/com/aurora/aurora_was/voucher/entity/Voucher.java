package com.aurora.aurora_was.voucher.entity;

import com.aurora.aurora_was.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voucher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 어떤 회원의 지갑인지 연결! (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int remainingCount; // 남은 횟수 (예: 10회)

//    @Builder
//    public Voucher(Member member, int remainingCount) {
//        this.member = member;
//        this.remainingCount = remainingCount;
//    }

    // 🚀 예약할 때 횟수 1회 차감하는 마법의 버튼!
    public void deductCount() {
        if (this.remainingCount <= 0) {
            throw new IllegalStateException("수강권 횟수가 부족합니다!");
        }
        this.remainingCount -= 1;
    }

    public void addCount(int count) {
        this.remainingCount += count;
    }
}
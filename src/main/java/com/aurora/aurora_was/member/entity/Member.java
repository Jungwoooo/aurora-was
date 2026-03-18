package com.aurora.aurora_was.member.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity // 💡 "Spring아, 이 클래스 보고 MySQL에 테이블 좀 만들어줘!" 라는 마법의 주문
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE member SET use_yn = 'N' WHERE id = ?")
@SQLRestriction("use_yn = 'Y'")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 숨기기 (안전성 UP)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id // 기본키 (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 1, 2, 3... 알아서 번호표 뽑기
    private Long id;

    @Column(nullable = false, unique = true) // 이메일은 필수고, 중복 가입 안 됨!
    private String email;

    @Column(nullable = false)
    private String password; // 나중에 꼭 암호화해서 넣어야 합니다!

    @Column(nullable = false, length = 20)
    private String name; // 이름 (홍길동)

    @Column(nullable = false, length = 20)
    private String phone; // 전화번호 (010-1234-5678)

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, name = "use_yn", length = 1)
    private String useYn;

    @PrePersist
    public void prePersist() {
        if (this.useYn == null) {
            this.useYn = "Y";
        }
    }

    // 💡 처음 회원가입 할 때 쓸 조립 설명서 (Builder)
//    @Builder
//    public Member(String email, String password, String name, String phone, String role) {
//        this.email = email;
//        this.password = password;
//        this.name = name;
//        this.phone = phone;
//        this.role = role;
//    }
}
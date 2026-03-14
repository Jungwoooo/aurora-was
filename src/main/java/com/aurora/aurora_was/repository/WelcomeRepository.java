package com.aurora.aurora_was.repository;

import com.aurora.aurora_was.entity.Welcome;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WelcomeRepository extends JpaRepository<Welcome, Long> {
    // 아무것도 안 적어도 됩니다. JpaRepository가 알아서 다 해줍니다!
}
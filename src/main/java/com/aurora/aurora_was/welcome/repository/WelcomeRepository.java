package com.aurora.aurora_was.welcome.repository;

import com.aurora.aurora_was.welcome.entity.Welcome;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface WelcomeRepository extends JpaRepository<Welcome, Long> {
    // 아무것도 안 적어도 됩니다. JpaRepository가 알아서 다 해줍니다!
}
package com.aurora.aurora_was.welcome.controller;

import com.aurora.aurora_was.welcome.entity.Welcome;
import com.aurora.aurora_was.welcome.repository.WelcomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private WelcomeRepository repository;

    @GetMapping("/test")
    public String test() {
        Welcome welcome = new Welcome();
        welcome.setMessage("DB 연결 성공! 헬로 폴댄스!");
        repository.save(welcome); // DB에 저장

        return repository.findAll().get(0).getMessage(); // DB에서 꺼내오기
    }
}
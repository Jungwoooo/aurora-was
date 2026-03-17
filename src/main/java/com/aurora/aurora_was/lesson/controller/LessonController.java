package com.aurora.aurora_was.lesson.controller;

import com.aurora.aurora_was.lesson.dto.res.SearchLessonRes;
import com.aurora.aurora_was.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/list")
    public ResponseEntity<List<SearchLessonRes>> getLessonsByDate(@RequestParam String date) {
        List<SearchLessonRes> lessons = lessonService.getLessonsByDate(date);
        return ResponseEntity.ok(lessons);
    }
}
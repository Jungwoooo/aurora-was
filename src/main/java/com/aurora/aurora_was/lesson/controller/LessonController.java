package com.aurora.aurora_was.lesson.controller;

import com.aurora.aurora_was.lesson.dto.req.CreateLessonReq;
import com.aurora.aurora_was.lesson.dto.req.SearchLessonReq;
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

    @PostMapping("/create")
    public ResponseEntity<String> createLesson(@RequestBody CreateLessonReq createLessonReq) {
        lessonService.createLesson(createLessonReq);
        return ResponseEntity.ok("수업이 성공적으로 개설되었습니다!");
    }

    @GetMapping("/list")
    public ResponseEntity<List<SearchLessonReq>> getLessonsByDate(@RequestParam String date) {
        List<SearchLessonReq> lessons = lessonService.getLessonsByDate(date);
        return ResponseEntity.ok(lessons);
    }
}
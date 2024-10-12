package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.entity.Feedback;
import com.shinhan.VRRS.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<Void> submitFeedback(@RequestParam("content") String content) {
        feedbackService.saveFeedback(content);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
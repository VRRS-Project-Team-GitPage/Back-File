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
    public ResponseEntity<Feedback> submitFeedback(@RequestParam("content") String content) {
        Feedback feedback = feedbackService.saveFeedback(content);
        return ResponseEntity.ok(feedback);
    }
}

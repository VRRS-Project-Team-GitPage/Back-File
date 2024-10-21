package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.service.FeedbackService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<Void> submitFeedback(@RequestBody @Size(max = 500) Map<String, String> request) {
        String content = request.get("content");

        feedbackService.saveFeedback(content);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.FeedbackDTO;
import com.shinhan.VRRS.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<Void> submitFeedback(@Valid @RequestBody FeedbackDTO request) {
        String content = request.getContent().trim(); // 앞뒤 공백 제거

        feedbackService.saveFeedback(request.getType(), content);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}
package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.Feedback;
import com.shinhan.VRRS.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void saveFeedback(String type, String content) {
        feedbackRepository.save(new Feedback(type, content));
    }
}
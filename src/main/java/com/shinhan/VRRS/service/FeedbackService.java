package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.Feedback;
import com.shinhan.VRRS.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public Feedback saveFeedback(String content) {
        Feedback feedback = new Feedback(content);
        return feedbackRepository.save(feedback);
    }
}
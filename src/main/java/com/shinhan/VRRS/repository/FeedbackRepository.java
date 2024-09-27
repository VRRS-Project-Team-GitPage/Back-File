package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
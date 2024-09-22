package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ReviewDTO;
import com.shinhan.VRRS.entity.CompositePK;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.repository.ReviewRepository;
import com.shinhan.VRRS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewDetailsService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // 최신 리뷰 3개 조회
    public List<ReviewDTO> getLatest3Review(Long proId, Long userId) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Review> reviews = reviewRepository.findLatest3ByProIdAndUserIdNot(proId, userId, pageable);
        List<ReviewDTO> result = new ArrayList<>();

        // 닉네임 및 채식유형 조회
        for (Review review : reviews) {
            User user = userRepository.findById(review.getUserId()).orElse(null);
            if (user != null) result.add(new ReviewDTO(review, user));
        }
        return result;
    }

    // 사용자 리뷰 조회
    public ReviewDTO getReview(Long proId, Long userId) {
        CompositePK compositePK = new CompositePK(proId, userId);
        Review review = reviewRepository.findById(compositePK).orElse(null);
        if (review != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) return new ReviewDTO(review, user);
        }
        return null;
    }
}

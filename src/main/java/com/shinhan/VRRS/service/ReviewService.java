package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.CompositePK;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    // 리뷰 저장 메서드
    @Transactional
    public Review saveReview(Long proId, Long userId, String content, boolean isRec) {
        Review review = new Review(proId, userId, content, isRec);
        return reviewRepository.save(review);
    }

    // 리뷰 수정 메서드
    @Transactional
    public Review updateReview(Long proId, Long userId, String newContent, boolean isRec) {
        CompositePK compositePK = new CompositePK(proId, userId);

        // 기존 리뷰가 있는지 확인
        Optional<Review> optionalReview = reviewRepository.findById(compositePK);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setContent(newContent); // 내용 수정
            review.setRec(isRec); // 추천 여부 수정
            review.setChange(true); // 수정 여부 수정
            review.setDate(LocalDateTime.now()); // 수정 날짜를 현재 날짜로 변경
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("리뷰를 찾을 수 없습니다.");
        }
    }

    // 리뷰 삭제 메서드
    @Transactional
    public void deleteReview(Long proId, Long userId) {
        CompositePK compositePK = new CompositePK(proId, userId);

        // 해당 리뷰가 있는지 확인하고 삭제
        if (reviewRepository.existsById(compositePK)) {
            reviewRepository.deleteById(compositePK);
        } else {
            throw new RuntimeException("리뷰를 찾을 수 없습니다.");
        }
    }
}
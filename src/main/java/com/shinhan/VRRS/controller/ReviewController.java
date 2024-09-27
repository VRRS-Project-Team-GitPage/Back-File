package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping("/submit")
    public ResponseEntity<Review> submitReview(@RequestParam("pro-id") Long proId,
                                               @RequestParam("user-id") Long userId,
                                               @RequestParam("content") String content,
                                               @RequestParam("rec") boolean isRec) {
        Review review = reviewService.saveReview(proId, userId, content, isRec);
        return ResponseEntity.ok(review);
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<Review> updateReview(@RequestParam("pro-id") Long proId,
                                               @RequestParam("user-id") Long userId,
                                               @RequestParam("content") String content,
                                               @RequestParam("rec") boolean isRec) {
        Review updatedReview = reviewService.updateReview(proId, userId, content, isRec);
        return ResponseEntity.ok(updatedReview);
    }

    // 리뷰 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReview(@RequestParam("pro-id") Long proId,
                                               @RequestParam("user-id") Long userId) {
        reviewService.deleteReview(proId, userId);
        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }
}
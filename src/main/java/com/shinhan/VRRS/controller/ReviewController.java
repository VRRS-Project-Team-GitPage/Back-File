package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductReview;
import com.shinhan.VRRS.dto.ReviewDTO;
import com.shinhan.VRRS.dto.ReviewRequest;
import com.shinhan.VRRS.dto.UserReview;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 제품 리뷰 조회
    @GetMapping("/product/{proId}")
    public ResponseEntity<ProductReview> getProductReviews(@PathVariable("proId") Long proId,
                                                           @RequestParam("userId") Long userId,
                                                           @RequestParam(name = "sort", defaultValue = "asc") String sort) {
        ReviewDTO review = reviewService.getUerReview(proId, userId);
        List<Review> reviews = reviewService.getProductReviews(proId, userId, sort);

        if (review == null && reviews.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(new ProductReview(review, reviewService.getProductReviews(reviews)));
    }

    // 사용자 리뷰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReview>> getReviewsByUserId(@PathVariable("userId") Long userId,
                                                               @RequestParam(name = "sort", defaultValue = "asc") String sort) {
        List<Review> reviews = reviewService.getUserReviews(userId, sort);
        if (reviews.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(reviewService.getUserReviews(reviews));
    }

    // 리뷰 등록
    @PostMapping("/submit")
    public ResponseEntity<Review> submitReview(@RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.saveReview(
                    request.getProId(), request.getUserId(), request.getContent(), request.isRec()
            );
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<Review> updateReview(@RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.updateReview(
                    request.getProId(), request.getUserId(), request.getContent(), request.isRec()
            );
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteReview(@RequestParam("proId") Long proId,
                                             @RequestParam("userId") Long userId) {
        try {
            reviewService.deleteReview(proId, userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}
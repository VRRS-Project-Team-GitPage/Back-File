package com.shinhan.VRRS.controller;

import com.shinhan.VRRS.dto.ProductReview;
import com.shinhan.VRRS.dto.ReviewDTO;
import com.shinhan.VRRS.dto.ReviewRequest;
import com.shinhan.VRRS.dto.UserReview;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.service.ReviewService;
import com.shinhan.VRRS.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    private final UserService userService;

    // 제품 리뷰 조회
    @GetMapping("/product")
    public ResponseEntity<ProductReview> getProductReviews(@RequestHeader("Authorization") String jwt,
                                                           @RequestParam("proId") @Min(1) Long proId,
                                                           @RequestParam(name="sort", defaultValue = "asc") String sort) {
        if (!sort.equals("asc") && !sort.equals("desc"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request

        Long userId = userService.getUserFromJwt(jwt).getId();

        ReviewDTO review = reviewService.getUerReview(proId, userId);
        List<Review> reviews = reviewService.getProductReviews(proId, userId, sort);

        if (review == null && reviews.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(new ProductReview(review, reviewService.getProductReviews(reviews)));
    }

    // 사용자 리뷰 조회
    @GetMapping("/user")
    public ResponseEntity<List<UserReview>> getReviewsByUserId(@RequestHeader("Authorization") String jwt) {
        Long userId = userService.getUserFromJwt(jwt).getId();

        List<Review> reviews = reviewService.getUserReviews(userId);
        if (reviews.isEmpty()) return ResponseEntity.noContent().build(); // 204 No Content
        return ResponseEntity.ok(reviewService.getUserReviews(reviews));
    }

    // 리뷰 등록
    @PostMapping("/submit")
    public ResponseEntity<Review> submitReview(@RequestHeader("Authorization") String jwt,
                                               @Valid @RequestBody ReviewRequest request) {
        try {
            Long userId = userService.getUserFromJwt(jwt).getId();
            Review review = reviewService.saveReview(request.getProId(), userId, request.getContent(), request.isRec());
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<Review> updateReview(@RequestHeader("Authorization") String jwt,
                                               @Valid @RequestBody ReviewRequest request) {
        try {
            Long userId = userService.getUserFromJwt(jwt).getId();
            Review review = reviewService.updateReview(request.getProId(), userId, request.getContent(), request.isRec());
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteReview(@RequestHeader("Authorization") String jwt,
                                             @RequestParam("proId") @Min(0) Long proId) {
        try {
            Long userId = userService.getUserFromJwt(jwt).getId();
            reviewService.deleteReview(proId, userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}
package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ReviewDTO;
import com.shinhan.VRRS.dto.ect.PreviewReview;
import com.shinhan.VRRS.dto.response.UserReviewResponse;
import com.shinhan.VRRS.entity.*;
import com.shinhan.VRRS.repository.ProductRepository;
import com.shinhan.VRRS.repository.ReviewRepository;
import com.shinhan.VRRS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 리뷰 찾기 메서드
    private Review findReviewById(Long proId, Long userId) {
        CompositePK compositePK = new CompositePK(proId, userId);
        return reviewRepository.findById(compositePK)
                .orElseThrow(NoSuchElementException::new);
    }

    // 제품 찾기 메서드
    private Product findProductById(Long proId) {
        return productRepository.findById(proId)
                .orElseThrow(NoSuchElementException::new);
    }

    public List<ReviewDTO> convertToReviewDTO(List<Review> reviews) {
        List<ReviewDTO> result = new ArrayList<>();

        // 닉네임 및 채식유형 조회
        for (Review review : reviews)
            userRepository.findById(review.getUserId())
                    .ifPresent(user -> result.add(new ReviewDTO(review, user)));

        return result;
    }

    public List<UserReviewResponse> getUserReviews(List<Review> reviews) {
        List<UserReviewResponse> result = new ArrayList<>();

        // 제품명 조회
        for (Review review : reviews)
            productRepository.findById(review.getProId())
                    .ifPresent(product -> result.add(new UserReviewResponse(review, product)));

        return result;
    }

    // 최신 리뷰 3개 조회
    public PreviewReview getPreviewReview(Long proId, Long userId) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Review> reviews = reviewRepository.findLatestPreviewByProId(proId, pageable);
        List<ReviewDTO> previewReview = new ArrayList<>();

        PreviewReview result = new PreviewReview();

        // 닉네임 및 채식유형 조회
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            User user = userRepository.findById(review.getUserId()).orElse(null);

            if (user != null) {
                // 사용자 리뷰 인덱스 설정
                if (Objects.equals(user.getId(), userId))
                    result.setReviewIndex(i);
                previewReview.add(new ReviewDTO(review, user));
            }
        }
        result.setReviews(previewReview);
        return result;
    }

    // 사용자 리뷰 조회
    public ReviewDTO getUerReview(Long proId, Long userId) {
        CompositePK compositePK = new CompositePK(proId, userId);
        Review review = reviewRepository.findById(compositePK).orElse(null);
        if (review != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null)
                return new ReviewDTO(review, user);
        }
        return null;
    }

    // 정렬 기준에 따라 제품 리뷰 조회
    public List<Review> getProductReviews(Long proId, Long userId, String sortOrder) {
        Sort sort = Sort.by("date");
        if ("desc".equalsIgnoreCase(sortOrder))
            sort = sort.descending();
        else
            sort = sort.ascending();

        return reviewRepository.findByProIdAndUserIdNot(proId, userId, sort);
    }

    // 정렬 기준에 따라 사용자 리뷰 조회
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByDateDesc(userId);
    }

    // 리뷰 저장 메서드
    @Transactional
    public Review saveReview(Long proId, Long userId, String content, boolean isRec) {
        Product product = findProductById(proId);

        // 리뷰 확인
        CompositePK id = new CompositePK(proId, userId);
        if (reviewRepository.existsById(id))
            throw new IllegalArgumentException();

        // 리뷰 저장
        Review review = new Review(proId, userId, content, isRec);
        Review savedReview = reviewRepository.save(review);

        // 추천수 수정
        updateProductReviewCounts(product, isRec ? 1 : 0, isRec ? 0 : 1);

        return savedReview;
    }

    // 리뷰 수정 메서드
    @Transactional
    public Review updateReview(Long proId, Long userId, String content, boolean isRec) {
        Product product = findProductById(proId);
        Review review = findReviewById(proId, userId);

        // 이전 상태 저장
        boolean previousRec = review.isRec();

        // 리뷰 수정
        review.setContent(content);
        review.setRec(isRec);
        review.setChange(true);
        review.setDate(LocalDateTime.now());

        // 추천수 수정
        if (previousRec != isRec)
            updateProductReviewCounts(product, isRec ? 1 : -1, isRec ? -1 : 1);

        return reviewRepository.save(review);
    }

    // 리뷰 삭제 메서드
    @Transactional
    public void deleteReview(Long proId, Long userId) {
        Product product = findProductById(proId);
        Review review = findReviewById(proId, userId);

        // 리뷰 삭제
        reviewRepository.deleteById(new CompositePK(proId, userId));

        // 추천수 수정
        updateProductReviewCounts(product, review.isRec() ? -1 : 0, review.isRec() ? 0 : -1);
    }

    // 리뷰수 및 추천수 수정 메서드
    @Transactional
    private void updateProductReviewCounts(Product product, int recDelta, int notRecDelta) {
        product.setRecCnt(product.getRecCnt() + recDelta);
        product.setNotRecCnt(product.getNotRecCnt() + notRecDelta);
        productRepository.save(product);
    }
}
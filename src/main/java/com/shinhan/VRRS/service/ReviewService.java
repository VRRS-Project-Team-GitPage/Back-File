package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.ReviewDTO;
import com.shinhan.VRRS.dto.UserReview;
import com.shinhan.VRRS.entity.CompositePK;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.User;
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
import java.util.Optional;

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
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    // 제품 찾기 메서드
    private Product findProductById(Long proId) {
        return productRepository.findById(proId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<ReviewDTO> getProductReviews(List<Review> reviews) {
        List<ReviewDTO> result = new ArrayList<>();

        // 닉네임 및 채식유형 조회
        for (Review review : reviews)
            userRepository.findById(review.getUserId())
                    .ifPresent(user -> result.add(new ReviewDTO(review, user)));

        return result;
    }

    public List<UserReview> getUserReviews(List<Review> reviews) {
        List<UserReview> result = new ArrayList<>();

        // 제품명 조회
        for (Review review : reviews)
            productRepository.findById(review.getProId())
                    .ifPresent(product -> result.add(new UserReview(review, product)));

        return result;
    }

    // 최신 리뷰 3개 조회
    public List<ReviewDTO> getPreviewReview(Long proId, Long userId) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Review> reviews = reviewRepository.findLatestPreviewByProIdAndUserIdNot(proId, userId, pageable);
        List<ReviewDTO> result = new ArrayList<>();

        // 닉네임 및 채식유형 조회
        for (Review review : reviews)
            userRepository.findById(review.getUserId())
                    .ifPresent(user -> result.add(new ReviewDTO(review, user)));

        return result;
    }

    // 사용자 리뷰 조회
    public ReviewDTO getUerReview(Long proId, Long userId) {
        CompositePK compositePK = new CompositePK(proId, userId);
        Review review = reviewRepository.findById(compositePK).orElse(null);
        if (review != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) return new ReviewDTO(review, user);
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
    public List<Review> getUserReviews(Long userId, String sortOrder) {
        Sort sort = Sort.by("date");
        if ("desc".equalsIgnoreCase(sortOrder))
            sort = sort.descending();
        else
            sort = sort.ascending();

        return reviewRepository.findByUserId(userId, sort);
    }

    // 리뷰 저장 메서드
    @Transactional
    public Review saveReview(Long proId, Long userId, String content, boolean isRec) throws RuntimeException {
        Product product = findProductById(proId);

        Review review = new Review(proId, userId, content, isRec);
        Review savedReview = reviewRepository.save(review);

        // 리뷰수 및 추천수 수정
        updateProductReviewCounts(product, (content == null) ? 0 : 1, isRec ? 1 : 0, isRec ? 0 : 1);

        return savedReview;
    }

    // 리뷰 수정 메서드
    @Transactional
    public Review updateReview(Long proId, Long userId, String content, boolean isRec) throws RuntimeException {
        Product product = findProductById(proId);
        Review review = findReviewById(proId, userId);

        // 이전 상태 저장
        boolean previousContent = (review.getContent() == null);
        boolean previousRec = review.isRec();

        // 현재 상태 저장
        int reviewChange = (content == null) ? -1 : 1;
        int recChange = isRec ? 1 : -1;
        int notRecChange = isRec ? -1 : 1;

        // 리뷰 수정
        review.setContent(content);
        review.setRec(isRec);
        review.setChange(true);
        review.setDate(LocalDateTime.now());

        // 리뷰수 및 추천수 수정
        if (previousRec != isRec) {
            if (previousContent != (content == null))
                updateProductReviewCounts(product, reviewChange, recChange, notRecChange);
            else
                updateProductReviewCounts(product, 0, recChange, notRecChange);
        } else if (previousContent != (content == null)) {
            updateProductReviewCounts(product, reviewChange, 0, 0);
        }

        return reviewRepository.save(review);
    }

    // 리뷰 삭제 메서드
    @Transactional
    public void deleteReview(Long proId, Long userId) throws RuntimeException {
        Product product = findProductById(proId);
        Review review = findReviewById(proId, userId);

        reviewRepository.deleteById(new CompositePK(proId, userId));

        // 리뷰수 및 추천수 수정
        updateProductReviewCounts(
                product, (review.getContent() == null) ? 0 : -1, review.isRec() ? -1 : 0, review.isRec() ? 0 : -1
        );
    }

    // 리뷰수 및 추천수 수정 메서드
    @Transactional
    private void updateProductReviewCounts(Product product, int reviewDelta, int recDelta, int notRecDelta) {
        product.setReviewCnt(product.getReviewCnt() + reviewDelta);
        product.setRecCnt(product.getRecCnt() + recDelta);
        product.setNotRecCnt(product.getNotRecCnt() + notRecDelta);
        productRepository.save(product);
    }
}
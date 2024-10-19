package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.CompositePK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, CompositePK> {
    // 특정 사용자의 리뷰 조회
    List<Review> findByUserId(Long userId);

    // 특정 제품의 모든 리뷰 조회
    List<Review> findByProIdAndUserIdNot(Long proId, Long userId, Sort sort);

    // 특정 사용자의 모든 리뷰 조회
    List<Review> findByUserIdOrderByDateAsc(Long userId);

    // 다른 사용자의 최신 리뷰 3개 가져오기
    @Query("SELECT r FROM Review r WHERE r.proId = :proId AND r.content IS NOT NULL AND r.userId != :userId ORDER BY r.date DESC")
    List<Review> findLatestPreviewByProIdAndUserIdNot(@Param("proId") Long proId, @Param("userId") Long userId, Pageable pageable);

    // 사용자가 비추천한 제품의 ID 목록 조회
    @Query("SELECT r.id.proId FROM Review r WHERE r.id.userId = :userId AND r.isRec = false")
    List<Long> findDislikedProductIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Product p " +
           "WHERE EXISTS (SELECT 1 FROM Review r WHERE r.proId = p.id AND r.userId = :userId AND r.isRec = true)")
    List<Product> findLikedProductIdsByUserId(@Param("userId") Long userId);
}
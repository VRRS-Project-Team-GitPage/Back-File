package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.CompositePK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, CompositePK> {
    // 다른 사용자의 최신 리뷰 3개 가져오기
    @Query("SELECT r FROM Review r WHERE r.proId = :proId AND r.content IS NOT NULL AND r.userId != :userId ORDER BY r.date DESC")
    List<Review> findLatestPreviewByProIdAndUserIdNot(@Param("proId") Long proId, @Param("userId") Long userId, Pageable pageable);

    List<Review> findByUserId(Long userId);
}
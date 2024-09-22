package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.CompositePK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, CompositePK> {


    // 특정 proId로 content가 null이 아니며 userId가 아닌 최신 3개의 리뷰 가져오기
    @Query("SELECT p FROM Review p WHERE p.proId = :proid AND p.content IS NOT NULL AND p.userId != :userid ORDER BY p.date DESC")
    List<Review> findLatest3ByProIdAndUserIdNot(@Param("proid") Long proId, @Param("userid") Long userId, Pageable pageable);
}
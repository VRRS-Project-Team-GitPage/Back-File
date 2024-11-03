package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Bookmark;
import com.shinhan.VRRS.entity.CompositePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, CompositePK> {
    @Query("SELECT b.proId FROM Bookmark b WHERE b.userId = :userId")
    List<Long> findProIdsByUserId(@Param("userId") Long userId);

    List<Bookmark> findByUserIdOrderByDateAsc(Long userId);
}
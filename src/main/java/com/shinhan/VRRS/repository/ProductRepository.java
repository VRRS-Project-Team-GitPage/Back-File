package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE REPLACE(p.name, ' ', '') LIKE %:name%")
    List<Product> findByCustomNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds")
    Slice<Product> findByCustomVegTypeId(@Param("vegTypeIds") List<Integer> vegTypeIds, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY (p.recCnt * 0.5 + p.bookmarkCnt) DESC")
    List<Product> findByTotalRank(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds ORDER BY (p.recCnt * 0.5 + p.bookmarkCnt) DESC")
    List<Product> findByVegTypeRank(@Param("vegTypeIds") List<Integer> vegTypeIds, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.recCnt = p.recCnt - 1 WHERE p.id = :proId")
    void decrementRecCut(@Param("proId") Long proId);

    @Modifying
    @Query("UPDATE Product p SET p.notRecCnt = p.notRecCnt - 1 WHERE p.id = :proId")
    void decrementNotRecCnt(@Param("proId") Long proId);

    @Modifying
    @Query("UPDATE Product p SET p.reviewCnt = p.reviewCnt - 1 WHERE p.id = :proId")
    void decrementReviewCnt(@Param("proId") Long proId);

    @Modifying
    @Query("UPDATE Product p SET p.bookmarkCnt = p.bookmarkCnt - 1 WHERE p.id = :proId")
    void decrementBookmarkCnt(@Param("proId") Long proId);
}
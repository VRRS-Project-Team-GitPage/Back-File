package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByReportNum(String reportNum);

    @Query("SELECT p FROM Product p WHERE REPLACE(p.name, ' ', '') LIKE %:name%")
    List<Product> findByCustomNameContaining(@Param("name") String name);

    Optional<Product> findByReportNum(String reportNum);

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds")
    Slice<Product> findByCustomVegTypeId(@Param("vegTypeIds") List<Integer> vegTypeIds, Pageable pageable);

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

    List<Product> findByCategoryId(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE p.id IN :proIds")
    List<Product> findByIds(@Param("proIds") List<Long> proIds);
}
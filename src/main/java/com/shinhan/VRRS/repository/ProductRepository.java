package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByReportNum(String reportNum);

    Optional<Product> findByReportNum(String reportNum);

    @Query("SELECT p FROM Product p WHERE REPLACE(p.name, ' ', '') LIKE %:name%")
    List<Product> findByCustomNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds")
    List<Product> findByVegTypeIds(@Param("vegTypeIds") List<Integer> vegTypeIds);

    @Query("SELECT p FROM Product p ORDER BY (p.recCnt * 0.5 + p.bookmarkCnt) DESC")
    List<Product> findAllOrderByPopularity();

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds ORDER BY (p.recCnt * 0.5 + p.bookmarkCnt) DESC")
    List<Product> findByVegTypeIdsOrderByPopularity(@Param("vegTypeIds") List<Integer> vegTypeIds);

    @Modifying
    @Query("UPDATE Product p SET p.recCnt = p.recCnt - 1 WHERE p.id = :proId")
    void decrementRecCut(@Param("proId") Long proId);

    @Modifying
    @Query("UPDATE Product p SET p.notRecCnt = p.notRecCnt - 1 WHERE p.id = :proId")
    void decrementNotRecCnt(@Param("proId") Long proId);

    @Modifying
    @Query("UPDATE Product p SET p.bookmarkCnt = p.bookmarkCnt - 1 WHERE p.id = :proId")
    void decrementBookmarkCnt(@Param("proId") Long proId);
}
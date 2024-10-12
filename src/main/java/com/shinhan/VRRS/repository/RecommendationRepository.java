package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.VegetarianType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY (p.recCnt * 0.5 + p.bookmarkCnt) DESC")
    List<Product> findByTotalRank(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds ORDER BY (p.recCnt * 0.5 + p.bookmarkCnt) DESC")
    List<Product> findByVegTypeRank(@Param("vegTypeIds") List<Integer> vegTypeIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.vegType.id IN :vegTypeIds")
    List<Product> findByCustomVegTypeId(@Param("vegTypeIds") List<Integer> vegTypeIds);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
            "AND p.vegType.id IN :vegTypeIds AND p.id NOT IN :excludedProductIds")
    List<Product> findByCategoryAndVegTypeInAndIdNotIn(@Param("categoryId") Integer categoryId,
                                                       @Param("vegTypeIds") List<Integer> vegTypeIds,
                                                       @Param("excludedProductIds") List<Long> excludedProductIds);

    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:keyword% OR p.ingredients LIKE %:keyword%) " +
            "AND p.vegType.id IN :vegTypeIds AND p.id NOT IN :excludedProductIds")
    List<Product> findByKeywordAndVegTypeInAndIdNotIn(@Param("keyword") String keyword,
                                                      @Param("vegTypeIds") List<Integer> vegTypeIds,
                                                      @Param("excludedProductIds") List<Long> excludedProductIds);
}
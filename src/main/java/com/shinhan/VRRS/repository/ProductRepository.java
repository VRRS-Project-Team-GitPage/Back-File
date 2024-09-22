package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByName(String name);

    List<Product> findByNameContaining(String name);

    @Query("SELECT p FROM Product p WHERE p.vegTypeId IN :vegTypeIds")
    Slice<Product> findByCustomVegTypeId(@Param("vegTypeIds") List<Integer> vegTypeIds, Pageable pageable);
}
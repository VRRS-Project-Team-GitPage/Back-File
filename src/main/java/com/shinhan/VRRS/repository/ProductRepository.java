package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByName(String name);
    List<Product> findByNameContaining(String name);
}
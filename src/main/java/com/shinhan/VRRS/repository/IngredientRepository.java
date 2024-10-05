package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Ingredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    @Query(value = "SELECT i.veg_type_id FROM ingredient i WHERE i.name LIKE CONCAT(:name, '%') ORDER BY LENGTH(i.name) ASC LIMIT 1", nativeQuery = true)
    Optional<Integer> findVegTypeByIngredientName(@Param("name") String name);
}

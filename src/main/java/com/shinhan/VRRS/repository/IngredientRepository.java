package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    @Query("SELECT i.vegTypeId FROM Ingredient i WHERE i.name LIKE CONCAT(:name, '%')")
    List<Integer> findVegTypeByIngredientName(@Param("name") String name);
}
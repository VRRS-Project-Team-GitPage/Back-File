package com.shinhan.VRRS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 제품명
    private String imgPath; // 제품 사진 경로
    private String description; // 제품 설명
    private String ingredients; // 제품
    private int proTypeId; // 제품 유형
    private int vegTypeId; // 채식 유형

    private int rec; // 추천수
    private int notRec; // 비추천수
    private int review; // 리뷰수

    // 이미지 저장을 위한 생성자
    public Product(String name, String description, String ingredients, int proTypeId, int vegTypeId) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.proTypeId = proTypeId;
        this.vegTypeId = vegTypeId;
    }
}

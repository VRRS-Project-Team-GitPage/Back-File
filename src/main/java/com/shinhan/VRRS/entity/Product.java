package com.shinhan.VRRS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 제품명
    private String imgPath; // 제품 사진 경로
    private String ingredients; // 원재료
    private int categoryId; // 카테고리
    private int vegTypeId; // 채식 유형

    private int recCnt; // 추천수
    private int notRecCnt; // 비추천수
    private int reviewCnt; // 리뷰수
}

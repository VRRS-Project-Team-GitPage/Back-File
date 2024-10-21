package com.shinhan.VRRS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // 제품명
    private String imgPath; // 이미지 경로
    private String ingredients; // 원재료
    private String reportNum; // 품목보고번호

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category; // 카테고리

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veg_type_id")
    private VegetarianType vegType; // 채식유형

    private int recCnt; // 추천수
    private int notRecCnt; // 비추천수
    private int reviewCnt; // 리뷰수
    private int bookmarkCnt; // 북마크수
}
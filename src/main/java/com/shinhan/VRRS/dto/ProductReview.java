package com.shinhan.VRRS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ProductReview {
    private ReviewDTO review; // 사용자 리뷰
    private List<ReviewDTO> reviews; // 전체 리뷰
}

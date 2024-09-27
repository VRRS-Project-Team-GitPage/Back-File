package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDetails {
    private String ingredients; // 원재료
    private boolean isBookmark; // 북마크 여부
    private ReviewDTO userReview; // 내 리뷰
    private List<ReviewDTO> reviews; // 리뷰 미리보기

    public ProductDetails(Product product) {
        this.ingredients = product.getIngredients();
    }
}
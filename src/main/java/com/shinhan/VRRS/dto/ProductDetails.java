package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ProductDetails extends ProductDTO {
    private String ingredients; // 원재료
    private String reportNum; // 품목보고번호
    private boolean bookmark; // 북마크 여부
    private List<ReviewDTO> reviews; // 리뷰 미리보기

    public ProductDetails(Product product) {
        super(product);
        this.ingredients = product.getIngredients();
        this.reportNum = product.getReportNum();
    }
}
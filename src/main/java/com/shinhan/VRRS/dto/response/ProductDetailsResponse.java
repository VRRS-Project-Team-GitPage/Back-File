package com.shinhan.VRRS.dto.response;

import com.shinhan.VRRS.dto.ProductDTO;
import com.shinhan.VRRS.dto.ReviewDTO;
import com.shinhan.VRRS.dto.ect.PreviewReview;
import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ProductDetailsResponse extends ProductDTO {
    private final String ingredients; // 원재료
    private final String reportNum; // 품목보고번호

    @Setter
    private boolean bookmark; // 북마크 여부

    private Integer reviewIndex; // 사용자 리뷰 인덱스
    private List<ReviewDTO> reviews; // 리뷰 미리보기

    public ProductDetailsResponse(Product product) {
        super(product);
        this.ingredients = product.getIngredients();
        this.reportNum = product.getReportNum();
    }

    public void setReviews(PreviewReview previewReview) {
        this.reviewIndex = previewReview.getReviewIndex();
        this.reviews = previewReview.getReviews();
    }
}
package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private int categoryId;
    private int vegTypeId;
    private int recCnt;
    private int notRecCnt;
    private int reviewCnt;
    private String imgUrl; // 이미지 URL

    public ProductDTO(Product product, String imgUrl) {
        this.id = product.getId();
        this.name = product.getName();
        this.categoryId = product.getCategoryId();
        this.vegTypeId = product.getVegTypeId();
        this.recCnt = product.getRecCnt();
        this.notRecCnt = product.getNotRecCnt();
        this.reviewCnt = product.getReviewCnt();
        this.imgUrl = imgUrl;
    }
}

package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String ingredients;
    private int proTypeId;
    private int vegTypeId;
    private int rec;
    private int notRec;
    private int review;
    private String imgUrl; // 이미지 URL

    public ProductDTO(Product product, String imgUrl) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.ingredients = product.getIngredients();
        this.proTypeId = product.getProTypeId();
        this.vegTypeId = product.getVegTypeId();
        this.rec = product.getRec();
        this.notRec = product.getNotRec();
        this.review = product.getReview();
        this.imgUrl = imgUrl;
    }
}

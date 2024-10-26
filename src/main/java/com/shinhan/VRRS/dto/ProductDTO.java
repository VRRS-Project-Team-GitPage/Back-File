package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Product;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Getter @Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String category; // 카테고리명
    private String vegType; // 채식유형명
    private int recCnt;
    private int notRecCnt;
    private String imgUrl; // 이미지 URL

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory().getName();
        this.vegType = product.getVegType().getName();
        this.recCnt = product.getRecCnt();
        this.notRecCnt = product.getNotRecCnt();

        // 이미지 URL 생성
        this.imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images").path(product.getImgPath()).toUriString();
    }
}
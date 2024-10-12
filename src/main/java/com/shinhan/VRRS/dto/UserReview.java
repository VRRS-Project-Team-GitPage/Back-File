package com.shinhan.VRRS.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserReview {
    private Long proId;
    private String proName;
    private String content;
    private boolean rec;
    private boolean change;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public UserReview(Review review, Product product) {
        this.proId = product.getId();
        this.proName = product.getName();
        this.content = review.getContent();
        this.rec = review.isRec();
        this.date = review.getDate();
    }
}

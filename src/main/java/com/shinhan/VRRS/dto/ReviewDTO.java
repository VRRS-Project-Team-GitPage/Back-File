package com.shinhan.VRRS.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.User;
import com.shinhan.VRRS.entity.VegetarianType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReviewDTO {
    private String nickname; // 닉네임
    private VegetarianType vegType; // 채식 유형
    private String content;
    private boolean rec;
    private boolean change;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDateTime date;

    public ReviewDTO(Review review, User user) {
        this.nickname = user.getNickname();
        this.vegType = user.getVegType();
        this.content = review.getContent();
        this.rec = review.isRec();
        this.date = review.getDate();
    }
}
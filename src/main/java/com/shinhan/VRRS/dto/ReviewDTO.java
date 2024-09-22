package com.shinhan.VRRS.dto;

import com.shinhan.VRRS.entity.Review;
import com.shinhan.VRRS.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private String nickname; // 닉네임
    private int vegTypeId; // 채식 유형
    private String content;
    private boolean isRec;
    private boolean isChange;
    private LocalDateTime date;

    public ReviewDTO(Review review, User user) {
        this.nickname = user.getNickname();
        this.vegTypeId = user.getVegTypeId();
        this.content = review.getContent();
        this.isRec = review.isRec();
        this.date = review.getDate();
    }
}

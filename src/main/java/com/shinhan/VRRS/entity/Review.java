package com.shinhan.VRRS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@IdClass(CompositePK.class)
public class Review {
    @Id
    private Long proId;
    @Id
    private Long userId;

    private String content; // 리뷰 내용
    private boolean isRec; // 추천 여부
    private boolean isChange; // 수정 여부
    private LocalDateTime date; // 작성 날짜

    public Review() {}

    public Review(Long proId, Long userId, String content, boolean isRec) {
        this.proId = proId;
        this.userId = userId;
        this.content = content;
        this.isRec = isRec;
        this.date = LocalDateTime.now();
    }

    public void setRec(boolean rec) {
        isRec = rec;
    }

    public void setChange(boolean change) {
        isChange = change;
    }
}
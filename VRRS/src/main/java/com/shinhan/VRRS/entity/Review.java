package com.shinhan.VRRS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@IdClass(CompositePK.class)
@Getter
@Setter
public class Review {
    @Id
    private Long proId; // 제품 ID
    @Id
    private Long userId; // 사용자 ID

    private String content; // 리뷰 내용
    private int isRec; // 추천 여부
    private int isChange; // 수정 여부
    private LocalDate date; // 작성 날짜
}

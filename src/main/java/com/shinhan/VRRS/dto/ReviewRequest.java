package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Long proId;
    private Long userId;
    private String content;
    private boolean rec;
}
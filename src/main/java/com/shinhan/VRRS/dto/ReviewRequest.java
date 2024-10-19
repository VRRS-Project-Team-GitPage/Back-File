package com.shinhan.VRRS.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewRequest {
    @NotNull
    @Min(1)
    private Long proId;
    private String content;
    @NotNull
    private boolean rec;
}
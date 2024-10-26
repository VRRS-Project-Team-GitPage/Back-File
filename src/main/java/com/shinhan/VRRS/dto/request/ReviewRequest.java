package com.shinhan.VRRS.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewRequest {
    @NotNull
    @Min(1)
    private Long proId;
    @NotBlank
    @Size(min = 1, max = 150)
    private String content;
    private boolean rec;
}
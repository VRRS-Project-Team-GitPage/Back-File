package com.shinhan.VRRS.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IngredientRequest {
    private String reportNum;
    @NotNull
    @Min(1) @Max(6)
    private Integer vegTypeId;
    @NotBlank
    private String ingredients;
    private boolean exists;
    private boolean fullBracket;
}